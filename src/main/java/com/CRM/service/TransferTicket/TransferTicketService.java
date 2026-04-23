package com.CRM.service.TransferTicket;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.CRM.Util.Helper.HelperService;
import com.CRM.model.DefectiveStock;
import com.CRM.model.Image;
import com.CRM.model.Inventory;
import com.CRM.model.InventoryTransaction;
import com.CRM.model.PatternColor;
import com.CRM.model.Product;
import com.CRM.model.Store;
import com.CRM.model.TransferTicket;
import com.CRM.model.TransferTicketColor;
import com.CRM.model.TransferTicketItem;
import com.CRM.model.Warehouse;
import com.CRM.repository.IDefectiveRepository;
import com.CRM.repository.IInventoryRepository;
import com.CRM.repository.IInventoryTransactionRepository;
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
import com.CRM.request.TransferTicket.ConfirmReceipt.ColorReceiptRequest;
import com.CRM.request.TransferTicket.ConfirmReceipt.ConfirmTransferTicketRequest;
import com.CRM.request.TransferTicket.ConfirmReceipt.ReceivedTransferItemRequest;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;
import com.CRM.response.TransferTicket.BaseTransferTicketResponse;
import com.CRM.response.TransferTicket.TransferProductResponse;
import com.CRM.response.TransferTicket.TransferTicketDetailResponse;
import com.CRM.response.TransferTicket.ConfirmReceipt.ComfirmReceiptResponse;
import com.CRM.service.Cloudinary.CloudinaryService;

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

    private final IInventoryTransactionRepository iInventoryTransactionRepository;

    private final IPatternColorRepository iPatternColorRepository;

    private final CloudinaryService cloudinaryService;

    private final IDefectiveRepository iDefectiveRepository;


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
        TransferTicket transferTicket = iTransferTicketRepository.findById(UUID.fromString(ticketId))
                                            .orElseThrow(() -> new IllegalArgumentException("Transfer ticket not found with id: " + ticketId));

        if (!"DAFT".equalsIgnoreCase(transferTicket.getStatus())) {
            throw new IllegalStateException("Only transfer tickets in 'DAFT' status can be confirmed for sending.");
        }

        Warehouse warehouse = transferTicket.getWarehouse();
        List<UUID> productIds = transferTicket.getItems().stream()
                                    .map(item -> item.getProduct().getId())
                                    .collect(Collectors.toList());
        Map<UUID, Integer> stockMap = iInventoryRepository.findAllByWarehouseIdAndProductIdIn(warehouse.getId(), productIds).stream()
                                    .collect(Collectors.toMap(inv -> inv.getProduct().getId(), Inventory::getQuantityOnHand));
        
        List<String> insufficient = new ArrayList<>();
        for(TransferTicketItem item : transferTicket.getItems()){
            int availableStock = stockMap.getOrDefault(item.getProduct().getId(), 0);
            if (availableStock < item.getQuantitySent()) {
                insufficient.add(String.format("'%s' (available: %d, requested: %d)", item.getProduct().getSkuCode(), availableStock, item.getQuantitySent()));
            }
        }
        if (!insufficient.isEmpty()) {
            throw new IllegalStateException("Insufficient stock for the following products: " + String.join(", ", insufficient));
        }
        
        transferTicket.setStatus("PENDING");
        transferTicket.setModifiedDate(new Date());
        iTransferTicketRepository.save(transferTicket);

        TransferTicketDetailResponse response = modelMapper.map(transferTicket, TransferTicketDetailResponse.class);
        return new APIResponse<>(response, "Confirmed sending transfer ticket successfully");
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

    @Override
    public APIResponse<Boolean> markInTransit(String ticketCode) {
        TransferTicket transferTicket = iTransferTicketRepository.findByTicketCode(ticketCode)
                                            .orElseThrow(() -> new IllegalArgumentException("Transfer ticket not found with code: " + ticketCode));

        if (!"PENDING".equals(transferTicket.getStatus())) {
            throw new IllegalArgumentException(
                "Only PENDING tickets can be marked IN_TRANSIT. Current status: " + transferTicket.getStatus());
        }

        Warehouse warehouse = transferTicket.getWarehouse();

        for(TransferTicketItem item : transferTicket.getItems()){
            Product product = item.getProduct();

            int totalSent = item.getQuantitySent();

            Inventory inventory = iInventoryRepository.findByWarehouseAndProduct(warehouse, product).orElseThrow(
                () -> new IllegalArgumentException("Inventory not found for product SKU: " + product.getSkuCode() + " in warehouse: " + warehouse.getName())
            );

            if (inventory.getQuantityOnHand() < totalSent) {
                throw new IllegalArgumentException(String.format("Insufficient stock for '%s' (stock: %d, demand: %d)", product.getProductDetail().getName(),
                    inventory.getQuantityOnHand(), totalSent));
            }

            inventory.setQuantityOnHand(inventory.getQuantityOnHand() - totalSent);

            iInventoryRepository.save(inventory);

            InventoryTransaction transaction = InventoryTransaction.builder()
                .product(product)
                .warehouse(warehouse)
                .store(null)
                .type("OUT_TRANSFER")
                .quantity(-totalSent)
                .referenceId(transferTicket.getId())
                .referenceCode(transferTicket.getTicketCode())
                .note("Issue warehouse receipt for transfer voucher: " + transferTicket.getTicketCode())
                .build();
            
            transaction.setInActive(true);
            transaction.setCode(randomCode());
            transaction.setDeleted(false);
            transaction.setDeletedAt(0L);
            transaction.setCreatedDate(new Date());
            transaction.setModifiedDate(new Date());
            iInventoryTransactionRepository.save(transaction);
        }

        transferTicket.setStatus("IN_TRANSIT");
        transferTicket.setModifiedDate(new Date());
        iTransferTicketRepository.save(transferTicket);

        return new APIResponse<>(true, "Goods shipped. Status: IN_TRANSIT.");
    }

    @Override
    @Transactional
    public APIResponse<Boolean> confirmReceivedAtDestination(String ticketCode, ConfirmTransferTicketRequest request) {
        TransferTicket transferTicket = iTransferTicketRepository.findByTicketCode(ticketCode)
                                            .orElseThrow(() -> new IllegalArgumentException("Transfer ticket not found with code: " + ticketCode));

        if (!List.of("IN_TRANSIT", "PARTIALLY_RECEIVED").contains(transferTicket.getStatus())){
            throw new IllegalArgumentException(
                "Unable to confirm receipt for ticket in this state: " + transferTicket.getStatus());
        }


        Map<String, TransferTicketItem> transferItemMap = transferTicket.getItems().stream()
                                            .collect(Collectors.toMap(item -> item.getProduct().getSkuCode(), item -> item));

        Map<UUID, TransferTicketColor> transferColorMap = transferTicket.getItems().stream()
                                            .flatMap(item -> item.getColorDetails().stream())
                                            .collect(Collectors.toMap(TransferTicketColor::getId, color -> color));
        
        request.getReceivedItems().forEach(r -> {
            TransferTicketItem poItem = transferItemMap.get(r.getSkuCode());
            if (poItem == null)
                throw new IllegalArgumentException(
                        "Sản phẩm không thuộc phiếu này: " + r.getSkuCode());

            r.getColors().forEach(colorReq -> {
                if (colorReq.getQuantityReceived() == null || colorReq.getQuantityReceived() < 0)
                    throw new IllegalArgumentException(
                            "Số lượng nhận không hợp lệ cho màu: " + colorReq.getColorId());

                UUID colorId = UUID.fromString(colorReq.getColorId());
                TransferTicketColor colorItem = transferColorMap.get(colorId);

                if (colorItem == null)
                    throw new IllegalArgumentException(
                            "TransferTicketColor not found: " + colorReq.getColorId());

                if (!colorItem.getTransferTicketItem().getProduct().getSkuCode().equals(r.getSkuCode()))
                    throw new IllegalArgumentException(String.format(
                            "Màu [%s] không thuộc sản phẩm [%s].",
                            colorReq.getColorId(), r.getSkuCode()));

                if (colorReq.getQuantityReceived() > colorItem.getRemainingQuantity())
                    throw new IllegalArgumentException(String.format(
                            "Số lượng nhận (%d) vượt quá số còn lại (%d) cho sản phẩm '%s' màu '%s'.",
                            colorReq.getQuantityReceived(),
                            colorItem.getRemainingQuantity(),
                            poItem.getProduct().getProductDetail() != null
                                    ? poItem.getProduct().getProductDetail().getName()
                                    : poItem.getProduct().getSkuCode(),
                            colorItem.getPatternColor() != null
                                    ? colorItem.getPatternColor().getLensColorName()
                                    : "Không màu"));
            });
        });
        
        Store store = transferTicket.getStore();

        if (store == null) {
            throw new IllegalArgumentException("Kho không hợp lệ.");
        }

        List<InventoryTransaction> transactions = new ArrayList<>();

        for(ReceivedTransferItemRequest receiveRequest: request.getReceivedItems()){
            Product product = transferItemMap.get(receiveRequest.getSkuCode()).getProduct();

            for(ColorReceiptRequest colorReq : receiveRequest.getColors()){
                TransferTicketColor colorItem = transferColorMap.get(UUID.fromString(colorReq.getColorId()));

                int qReceive = colorReq.getQuantityReceived();

                colorItem.setQuantityReceived(colorItem.getQuantityReceived() + qReceive);
                // if (colorItem.getRemainingQuantity() > 0) {
                //         throw new IllegalArgumentException(String.format(
                //                 "Phiếu %s, sản phẩm %s, màu %s: còn thiếu %d",
                //                 transferTicket.getTicketCode(),
                //                 product.getSkuCode(),
                //                 colorItem.getPatternColor() != null
                //                         ? colorItem.getPatternColor().getLensColorName()
                //                         : "Không màu",
                //                 colorItem.getRemainingQuantity()
                //         ));
                // }

                if (qReceive > 0) {
                    Inventory inventory = iInventoryRepository.findByProductAndStoreWithLock(product, store).orElseGet(() ->{
                            Inventory newInv = Inventory.builder()
                                .product(product)
                                .store(store)
                                .warehouse(null)
                                .quantityOnHand(0)
                                .build();
                            newInv.setInActive(true);
                            newInv.setCode(randomCode());
                            newInv.setDeleted(false);
                            newInv.setDeletedAt(0L);
                            newInv.setCreatedDate(new Date());
                            newInv.setModifiedDate(new Date());
                            return newInv;   
                    });

                    inventory.setQuantityOnHand(inventory.getQuantityOnHand() + qReceive);
                    inventory.setModifiedDate(new Date());
                    iInventoryRepository.save(inventory);

                    InventoryTransaction transaction = InventoryTransaction.builder()
                        .product(product)
                        .warehouse(null)
                        .store(store)
                        .type("IN_TRANSFER")
                        .quantity(qReceive)
                        .referenceId(transferTicket.getId())
                        .referenceCode(transferTicket.getTicketCode())
                        .note("Nhập kho hàng chuyển từ phiếu: " + transferTicket.getTicketCode())
                        .build();

                    transaction.setInActive(true);
                    transaction.setCode(randomCode());
                    transaction.setDeleted(false);
                    transaction.setDeletedAt(0L);
                    transaction.setCreatedDate(new Date());
                    transaction.setModifiedDate(new Date());
                    transactions.add(transaction);
                }

            }
        }

 // 5. TÍNH TOÁN LẠI MISSING LIST SAU KHI ĐÃ SET QUANTITY RECEIVED
    List<String> missingList = transferTicket.getItems().stream()
            .flatMap(item -> item.getColorDetails().stream())
            .filter(c -> c.getRemainingQuantity() > 0)
            .map(c -> String.format("%s: thiếu %d", 
                c.getTransferTicketItem().getProduct().getSkuCode(), 
                c.getRemainingQuantity()))
            .collect(Collectors.toList());

        String receiptNote = missingList.isEmpty()
            ? "FULL"
            : "MISS:\n" + String.join("\n", missingList);
        if (request.getNote() != null && !request.getNote().isBlank())
            receiptNote += "\nNote: " + request.getNote();

        transferTicket.setStatus(missingList.isEmpty() ? "COMPLETED" : "PARTIALLY_RECEIVED");
        transferTicket.setModifiedDate(new Date());
        transferTicket.setNote(receiptNote);

        if (missingList.isEmpty() || transferTicket.getConfirmedAt() == null) {
            transferTicket.setConfirmedAt(LocalDateTime.now());
        }

        iInventoryTransactionRepository.saveAll(transactions);
        iTransferTicketRepository.save(transferTicket); // cascade → tự save colorDetails

         String message = missingList.isEmpty()
            ? "Phiếu " + transferTicket.getTicketCode() + " hoàn tất. Đã nhận đủ hàng."
            : "Phiếu " + transferTicket.getTicketCode() + " đã ghi nhận. Còn "
                    + missingList.size() + " màu chưa đủ.";

        return new APIResponse<>(true, message);
    }

    @Override
    public APIResponse<ComfirmReceiptResponse> getInforConfirmReceipt(String ticketCode) {
        TransferTicket transferTicket = iTransferTicketRepository.findByTicketCode(ticketCode)
                                            .orElseThrow(() -> new IllegalArgumentException("Transfer ticket not found with code: " + ticketCode));

        ComfirmReceiptResponse response = modelMapper.map(transferTicket, ComfirmReceiptResponse.class);
        return new APIResponse<>(response, "Get information for confirm receipt successfully");
    }

    
}
