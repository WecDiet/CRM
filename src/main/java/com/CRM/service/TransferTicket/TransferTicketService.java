package com.CRM.service.TransferTicket;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.CRM.Util.Helper.HelperService;
import com.CRM.model.Inventory;
import com.CRM.model.PatternColor;
import com.CRM.model.Product;
import com.CRM.model.Store;
import com.CRM.model.TransferTicket;
import com.CRM.model.TransferTicketColor;
import com.CRM.model.TransferTicketItem;
import com.CRM.model.Warehouse;
import com.CRM.repository.IInventoryRepository;
import com.CRM.repository.IPatternColorRepository;
import com.CRM.repository.IProductRepository;
import com.CRM.repository.IStoreRepository;
import com.CRM.repository.ITransferTicketRepository;
import com.CRM.repository.IWarehouseRepository;
import com.CRM.repository.Specification.ProductSpecification;
import com.CRM.repository.Specification.TransferTicketSpecification;
import com.CRM.request.TransferTicket.TransferTicketColorRequest;
import com.CRM.request.TransferTicket.TransferTicketFilterRequest;
import com.CRM.request.TransferTicket.TransferTicketItemRequest;
import com.CRM.request.TransferTicket.TransferTicketRequest;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;
import com.CRM.response.TransferTicket.BaseTransferTicketResponse;
import com.CRM.response.TransferTicket.TransferProductResponse;
import com.CRM.response.TransferTicket.TransferTicketDetailResponse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransferTicketService extends HelperService<TransferTicket, UUID> implements ITransferTicketService {

    private final ITransferTicketRepository iTransferTicketRepository;

    private final IWarehouseRepository iWarehouseRepository;

    private final IStoreRepository iStoreRepository;

    private final ModelMapper modelMapper;

    private final IProductRepository iProductRepository;

    private final IInventoryRepository iInventoryRepository;

    private final IPatternColorRepository iPatternColorRepository;


    @Override
    public PagingResponse<BaseTransferTicketResponse> getAllTransferTicket(int page, int limit, String sortBy, String direction, TransferTicketFilterRequest filter) {
        return getAll(
                    page, 
                    limit, 
                    sortBy, 
                    direction, 
                    TransferTicketSpecification.getAllTransferTicket(filter),
                    BaseTransferTicketResponse.class,
                    iTransferTicketRepository);
    }

    @Override
    @Transactional
    public APIResponse<Boolean> createTransferTicket(TransferTicketRequest transferTicketRequest) {

        Warehouse warehouse = iWarehouseRepository.findById(UUID.fromString(transferTicketRequest.getWarehouseId())).orElseThrow(
            () -> new IllegalArgumentException("Warehouse not found.")
        );

        if (!"MAIN".equalsIgnoreCase(warehouse.getWarehouseType())) {
            throw new IllegalArgumentException("Only the Main Warehouse can ship goods.");
        }

        Store store = iStoreRepository.findById(UUID.fromString(transferTicketRequest.getStoreId())).orElseThrow(
            () -> new IllegalArgumentException("Store not found.")
        );
        
        List<String> skuCodes = transferTicketRequest.getItems().stream()
                            .map(TransferTicketItemRequest::getSkuCode)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
        
        Map<String, Product> productMap = iProductRepository.findBySkuCodeIn(skuCodes).stream()
                                .collect(Collectors.toMap(Product::getSkuCode, product -> product));
        
        List<String> notFoundSku = skuCodes.stream()
                                .filter(sku -> !productMap.containsKey(sku))
                                .collect(Collectors.toList());
        if (!notFoundSku.isEmpty()) {
            throw new IllegalArgumentException("Products not found for SKU codes: " + String.join(", ", notFoundSku));
        }

        List<UUID> colorIds = transferTicketRequest.getItems().stream()
                                .flatMap(item -> item.getColors().stream())
                                .map(TransferTicketColorRequest::getPatternColorId)
                                .filter(this::isValidUUID)
                                .map(UUID::fromString)
                                .distinct()
                                .collect(Collectors.toList());

        Map<UUID, PatternColor> colorMap = colorIds.isEmpty() ? Map.of() : iPatternColorRepository.findAllById(colorIds).stream()
                                .collect(Collectors.toMap(PatternColor::getId, color -> color));

        for(TransferTicketItemRequest itemRequest: transferTicketRequest.getItems()){
            Product product = productMap.get(itemRequest.getSkuCode());
            Set<UUID> validColors = product.getProductDetail().getColors().stream()
                                        .map(PatternColor::getId)
                                        .collect(Collectors.toSet());
            for(TransferTicketColorRequest colorRequest: itemRequest.getColors()){
                if (colorRequest.getPatternColorId() != null && isValidUUID(colorRequest.getPatternColorId())) {
                    UUID colorId = UUID.fromString(colorRequest.getPatternColorId());
                    if (!validColors.contains(colorId)) {
                        throw new IllegalArgumentException(String.format(
                                "Invalid color ID '%s' for product SKU '%s'", 
                                colorRequest.getPatternColorId(), 
                                itemRequest.getSkuCode()));
                    }

                    if (!colorMap.containsKey(colorId)) {
                        throw new IllegalArgumentException(String.format(
                                "Color not found for ID '%s'", 
                                colorRequest.getPatternColorId()));
                    }

                    if (colorRequest.getQuantitySent() == null || colorRequest.getQuantitySent() <= 0) {
                        throw new IllegalArgumentException(
                            "Quantity must be > 0 for color: " + colorRequest.getPatternColorId());
                    }
            
                }
            }
        }

        String date = LocalDate.now().toString().replace("-", "");
        TransferTicket transferTicket = modelMapper.map(transferTicketRequest, TransferTicket.class);
        transferTicket.setTicketCode("TT-" + generateUniqueCode() + "-" + date);
        transferTicket.setStore(store);
        transferTicket.setWarehouse(warehouse);
        transferTicket.setStatus("DAFT");
        transferTicket.setCode(randomCode());
        transferTicket.setInActive(true);
        transferTicket.setCreatedDate(new Date());
        transferTicket.setModifiedDate(new Date());
        transferTicket.setDeleted(false);
        transferTicket.setDeletedAt(0L);
        transferTicket.setItems(new ArrayList<>());

        List<TransferTicketItem> ticketItems = transferTicketRequest.getItems().stream()
                    .map(transferItem -> {
                        TransferTicketItem item = TransferTicketItem.builder()
                            .transferTicket(transferTicket)
                            .product(productMap.get(transferItem.getSkuCode()))
                            .build();
                        List<TransferTicketColor> ticketColors = transferItem.getColors().stream()
                            .map(colorRequest -> TransferTicketColor.builder()
                                .transferTicketItem(item)
                                .patternColor(isValidUUID(colorRequest.getPatternColorId()) ? colorMap.get(UUID.fromString(colorRequest.getPatternColorId())) : null)
                                .quantitySent(colorRequest.getQuantitySent())
                                .build()
                            ).collect(Collectors.toList());
                        item.setColorDetails(ticketColors);
                        return item;
                    }).collect(Collectors.toList());

        transferTicket.setItems(ticketItems);
        iTransferTicketRepository.save(transferTicket);
        return new APIResponse<>(true, "Created Tranfer Ticket successfully");
    }

    @Override
    public APIResponse<TransferTicketDetailResponse> getTransferTicketDetail(String id) {

        return getById(
                    UUID.fromString(id), 
                    iTransferTicketRepository, 
                    TransferTicket.class, 
                    TransferTicketDetailResponse.class);
    }

    @Override
    public APIResponse<TransferTicketDetailResponse> getTransferTicketInfor(String ticketCode) {
        TransferTicket transferTicket = iTransferTicketRepository.findByTicketCode(ticketCode)
                                            .orElseThrow(() -> new IllegalArgumentException("Transfer ticket not found with code: " + ticketCode));

        TransferTicketDetailResponse response = modelMapper.map(transferTicket, TransferTicketDetailResponse.class);
        return new APIResponse<>(response, "Get transfer ticket information successfully");
    }

    @Override
    public APIResponse<TransferTicketDetailResponse> confirmSendingTransferTicket(String ticketId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'confirmSendingTransferTicket'");
    }

    @Override
    public List<TransferProductResponse> getProductsForNewTicket(String keyword) {
        
        Specification<Product> spec = ProductSpecification.getProductsForNewTicket(keyword);
        List<Product> products = iProductRepository.findAll(spec);
        return products.stream()
            .map(p -> modelMapper.map(p, TransferProductResponse.class))
            .collect(Collectors.toList());
    }

    private boolean isValidUUID(String value) {
        if (value == null || value.isBlank() || value.equalsIgnoreCase("undefined")) {
            return false;
        }
        try {
            UUID.fromString(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public APIResponse<Boolean> deleteTransferTicket(String id) {
        TransferTicket transferTicket = iTransferTicketRepository.findById(UUID.fromString(id))
                                            .orElseThrow(() -> new IllegalArgumentException("Transfer ticket not found with id: " + id));
        transferTicket.setDeleted(true);
        transferTicket.setDeletedAt(System.currentTimeMillis());
        iTransferTicketRepository.save(transferTicket);
        return new APIResponse<>(true, "Deleted transfer ticket successfully");
    }

    @Override
    public void autoCleanTransferTicketTrash() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'autoCleanTransferTicketTrash'");
    }

    @Override
    public PagingResponse<BaseTransferTicketResponse> getAllTransferTicketTrash(int page, int limit, String sortBy,
            String direction, TransferTicketFilterRequest filter) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllTransferTicketTrash'");
    }

    @Override
    public APIResponse<TransferTicketDetailResponse> getTransferTicketTrashDetail(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTransferTicketTrashDetail'");
    }

    
}
