package com.CRM.service.PurchaseOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.CRM.Util.Helper.HelperService;
import com.CRM.enums.PurchaseOrderEnum;
import com.CRM.enums.RestoreEnum;
import com.CRM.model.Image;
import com.CRM.model.Inventory;
import com.CRM.model.Product;
import com.CRM.model.ProductDetail;
import com.CRM.model.PurchaseOrder;
import com.CRM.model.PurchaseOrderDelivery;
import com.CRM.model.PurchaseOrderItem;
import com.CRM.model.Supplier;
import com.CRM.model.Warehouse;
import com.CRM.repository.IInventoryRepository;
import com.CRM.repository.IProductRepository;
import com.CRM.repository.IPurchaseOrderDeliverryRepository;
import com.CRM.repository.IPurchaseOrderRepository;
import com.CRM.repository.ISupplierRepository;
import com.CRM.repository.IWarehouseRepository;
import com.CRM.repository.Specification.PurchaseSpecification;
import com.CRM.request.PurchaseOrder.OrderItemRequest;
import com.CRM.request.PurchaseOrder.PurchaseOrderFilterRequest;
import com.CRM.request.PurchaseOrder.PurchaseOrderRequest;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;
import com.CRM.response.PurchaseOrder.PurchaseOrderDetailResponse;
import com.CRM.response.PurchaseOrder.PurchaseOrderResponse;
import com.CRM.service.Cloudinary.CloudinaryService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService extends HelperService<PurchaseOrder, UUID> implements IPurchaseOrderService {

    private final IPurchaseOrderRepository iPurchaseOrderRepository;

    private final IWarehouseRepository iWarehouseRepository;

    private final IProductRepository iProductRepository;

    private final ISupplierRepository iSupplierRepository;

    private final IInventoryRepository iInventoryRepository;

    private final CloudinaryService cloudinaryService;

    private final IPurchaseOrderDeliverryRepository iPurchaseOrderDeliverryRepository;

    private final ModelMapper modelMapper;

    @Override
    public PagingResponse<PurchaseOrderResponse> getAllPurchaseOrder(int page, int limit, String sortBy, String direction, boolean active, PurchaseOrderFilterRequest filter) {
            return getAll(
                        page, 
                        limit, 
                        sortBy, 
                        direction, 
                        PurchaseSpecification.getAllPurchaseOrderByFilter(filter, active),
                        PurchaseOrderResponse.class, 
                        iPurchaseOrderRepository);
    }

    @Override
    public APIResponse<PurchaseOrderDetailResponse> getPurchaseOrderDetail(String id) {
        return getById(
                    UUID.fromString(id), 
                    iPurchaseOrderRepository, 
                    PurchaseOrder.class, 
                    PurchaseOrderDetailResponse.class);
    }

    @Override
    @Transactional
    public APIResponse<Boolean> createPurchaseOrder(PurchaseOrderRequest purchaseOrderRequest, List<MultipartFile> images, boolean active) throws BadRequestException {

        if (purchaseOrderRequest.getName() == null || purchaseOrderRequest.getName().isEmpty()) {
            throw new IllegalArgumentException("Purchase order name cannot be empty.");
        }

        if (iPurchaseOrderRepository.existsActiveByName(purchaseOrderRequest.getName())) {
            throw new IllegalArgumentException("Purchase order name already exists.");
        }

        if (purchaseOrderRequest.getOrderDay() != null && purchaseOrderRequest.getOrderMonth() != null && purchaseOrderRequest.getOrderYear() != null) {
            if (!isValidDate(purchaseOrderRequest.getOrderYear(), purchaseOrderRequest.getOrderMonth(), purchaseOrderRequest.getOrderDay())) {
                throw new BadRequestException("Invalid order date");
            }    
        }

        if (purchaseOrderRequest.getExpectedDeliveryDay() != null && purchaseOrderRequest.getExpectedDeliveryMonth() != null && purchaseOrderRequest.getExpectedDeliveryYear() != null) {
            if (!isValidDate(purchaseOrderRequest.getExpectedDeliveryYear(), purchaseOrderRequest.getExpectedDeliveryMonth(), purchaseOrderRequest.getExpectedDeliveryDay())) {
                throw new BadRequestException("Invalid expected delivery date");
            }    
        }
        
        try {
            Warehouse warehouse = iWarehouseRepository.findById(UUID.fromString(purchaseOrderRequest.getWarehouseId())).orElseThrow(
                () -> new IllegalArgumentException("Warehouse not found with id: " + purchaseOrderRequest.getWarehouseId())
            );

            if (!"MAIN".equalsIgnoreCase(warehouse.getWarehouseType())) {
                throw new IllegalArgumentException("The warehouse is not the main warehouse.");
            }
    
            Supplier supplier = iSupplierRepository.findById(UUID.fromString(purchaseOrderRequest.getSupplierId())).orElseThrow(
                () -> new IllegalArgumentException("Supplier not found with id: " + purchaseOrderRequest.getSupplierId())
            );

            LocalDate orderDate = null;

            Integer orderDay = purchaseOrderRequest.getOrderDay();
            Integer orderMonth = purchaseOrderRequest.getOrderMonth();
            Integer orderYear = purchaseOrderRequest.getOrderYear();
            
            LocalDate expectedDeliveryDate = null;

            Integer expectedDeliveryDay = purchaseOrderRequest.getExpectedDeliveryDay();
            Integer expectedDeliveryMonth = purchaseOrderRequest.getExpectedDeliveryMonth();
            Integer expectedDeliveryYear = purchaseOrderRequest.getExpectedDeliveryYear();

            if (orderDay != null && orderMonth != null && orderYear != null) {
                orderDate = LocalDate.of(orderYear, orderMonth, orderDay);
            }

            if (expectedDeliveryDay != null && expectedDeliveryMonth != null && expectedDeliveryYear != null) {
                expectedDeliveryDate = LocalDate.of(expectedDeliveryYear, expectedDeliveryMonth, expectedDeliveryDay);
            }

            PurchaseOrder purchaseOrder = modelMapper.map(purchaseOrderRequest, PurchaseOrder.class);
            purchaseOrder.setWarehouse(warehouse);
            purchaseOrder.setSupplier(supplier);
            purchaseOrder.setName(purchaseOrderRequest.getName());
            purchaseOrder.setStatus(PurchaseOrderEnum.fromString(purchaseOrderRequest.getStatus()).getStatus());
            purchaseOrder.setOrderDate(orderDate);
            purchaseOrder.setExpectedDeliveryDate(expectedDeliveryDate);
            purchaseOrder.setPoNumber("PO-" + generateUniqueCode() + "-" + orderYear);
            purchaseOrder.setDescription(purchaseOrderRequest.getDescription());

            purchaseOrder.setInActive(active);
            purchaseOrder.setCode(randomCode());
            purchaseOrder.setCreatedDate(new Date());
            purchaseOrder.setModifiedDate(new Date());
            purchaseOrder.setDeleted(false);
            purchaseOrder.setDeletedAt(0L);
    
            List<String> requestSkuCodes = purchaseOrderRequest.getItems().stream()
                    .map(OrderItemRequest::getSkuCode)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            
            List<Product> existingProducts = iProductRepository.findBySkuCodeIn(requestSkuCodes);

            Map<String, Product> productMap = existingProducts.stream()
                    .collect(Collectors.toMap(Product::getSkuCode, product -> product));
            

            List<Product> newProducts = new ArrayList<>();
            List<PurchaseOrderItem> orderItems = new ArrayList<>();
            BigDecimal totalAmount = BigDecimal.ZERO;
            String uploadedPublicId = null;

            for(int i = 0; i < purchaseOrderRequest.getItems().size(); i ++){

                OrderItemRequest items = purchaseOrderRequest.getItems().get(i);
                Product product = null;
                String skuCode = items.getSkuCode();
                if (skuCode != null && !skuCode.isEmpty()) {
                    product = productMap.get(skuCode);
                    if (product == null) {
                        throw new IllegalArgumentException("Product not found with SKU code: " + skuCode);
                    }

                    BigDecimal newSuggestedPrice = items.getUnitPrice().multiply(BigDecimal.valueOf(1.2));
                    if (newSuggestedPrice.compareTo(product.getProductDetail().getPrice()) > 0) {
                        product.getProductDetail().setPrice(newSuggestedPrice);
                    }
                } else {
                    if (items.getProductName() == null || items.getProductName().isEmpty()) {
                        throw new IllegalArgumentException("Product name is required for new product (skuCode is null).");
                    }

                    MultipartFile image = images.get(i);
                    
                    CompletableFuture<Map<String, Object>> uploadFuture = cloudinaryService.uploadMedia(image, "crm/products/main");
                    Map<String, Object> uploadResult = uploadFuture.join();
                    uploadedPublicId = (String) uploadResult.get("public_id");
                    String imageUrl = (String) uploadResult.get("secure_url");

                    Image imageProduct = Image.builder()
                            .imageUrl(imageUrl)
                            .publicId(uploadedPublicId)
                            .referenceId(supplier.getId())
                            .referenceType("PRODUCT")
                            .altText(supplier.getName())
                            .type("IMAGE")
                            .build();
                    
                    imageProduct.setInActive(active);
                    imageProduct.setCreatedDate(new Date());
                    imageProduct.setModifiedDate(new Date());
                    imageProduct.setCode(randomCode());
                    imageProduct.setDeletedAt(0L);
                    imageProduct.setDeleted(false);


                    Product newProduct = Product.builder()
                            .skuCode(generateUniqueCode())
                            .mainImage(imageProduct) 
                            .status(false)
                            .build();

                    newProduct.setInActive(active);
                    newProduct.setCode(randomCode());
                    newProduct.setCreatedDate(new Date());
                    newProduct.setModifiedDate(new Date());
                    newProduct.setDeleted(false);
                    newProduct.setDeletedAt(0L);

                    ProductDetail productDetail = ProductDetail.builder()
                            .product(newProduct)
                            .name(items.getProductName())
                            .manufacturer(supplier.getName())
                            .build();

                    newProduct.setProductDetail(productDetail);
                    newProducts.add(newProduct);
                    productMap.put(skuCode, newProduct);
                    product = newProduct;
                }

                BigDecimal itemTotal = calculateTotalAmount(items);
                totalAmount = totalAmount.add(itemTotal);

                PurchaseOrderItem orderItem = PurchaseOrderItem.builder()
                        .purchaseOrder(purchaseOrder)
                        .product(product)
                        .productName(items.getProductName())
                        .unitPrice(items.getUnitPrice())
                        .taxRate(items.getTaxRate())
                        .quantityOrdered(items.getQuantityOrdered())
                        .quantityReceived(0)
                        .build();
                orderItems.add(orderItem);
            }

            if (!newProducts.isEmpty()) {
                iProductRepository.saveAll(newProducts);
            }

            purchaseOrder.setItems(orderItems);
            purchaseOrder.setTotalAmount(totalAmount);
            
            iPurchaseOrderRepository.save(purchaseOrder);
            return new APIResponse<>(true, "Purchase order created successfully.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to create purchase order", e);
        }
    }


    private boolean isValidDate(Integer y, Integer m, Integer d) {
        try {
            LocalDate.of(y, m, d);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    
    private BigDecimal calculateTotalAmount(OrderItemRequest totalAmount) {
        // 1. Lấy đơn giá nhập
        BigDecimal unitPrice = totalAmount.getUnitPrice();
        
        // 2. Chuyển đổi số lượng (Integer) sang BigDecimal để tính toán
        BigDecimal quantity = BigDecimal.valueOf(totalAmount.getQuantityOrdered());
        
        // 3. Xử lý thuế suất (Tax Rate)
        // Nếu taxRate là 0.1 (10%), công thức tính là: UnitPrice * Quantity * (1 + 0.1)
        double taxRate = (totalAmount.getTaxRate() != null) ? totalAmount.getTaxRate() : 0.0;
        BigDecimal taxMultiplier = BigDecimal.valueOf(1.0 + taxRate);

        // 4. Thực hiện phép nhân: (UnitPrice * Quantity) * (1 + TaxRate)
        return unitPrice.multiply(quantity).multiply(taxMultiplier);
    }

    @Override
    public APIResponse<Boolean> updatePurchaseOrder(String id, PurchaseOrderRequest purchaseOrderRequest,
            boolean active) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updatePurchaseOrder'");
    }

    @Override
    @Transactional
    public APIResponse<Boolean> deletePurchaseOrder(String id) {
       PurchaseOrder purchaseOrder = iPurchaseOrderRepository.findById(UUID.fromString(id)).orElseThrow(
            () -> new IllegalArgumentException("Purchase order not found with id: " + id)
        );

        try {            
            if ("CANCELLED".equalsIgnoreCase(purchaseOrder.getStatus()) || "COMPLETED".equalsIgnoreCase(purchaseOrder.getPurchaseOrderDelivery().getStatus())) {
                purchaseOrder.setInActive(false);
                purchaseOrder.setDeleted(true);
                purchaseOrder.setDeletedAt(System.currentTimeMillis() / 1000);
                iPurchaseOrderRepository.save(purchaseOrder);
                return new APIResponse<>(true, "Purchase order deleted successfully."); 
            }

            return new APIResponse<>(false,  "Purchase order deleted fail. Because status Purchase Order other Completed or Cancelled ");
        } catch (Exception e) {
            return new APIResponse<>(false, "Delete purchase order failed.");
        }
    }

    @Override
    @Transactional
    @Scheduled(fixedRate = 60 * 1000) // Quét mõi 1 phút / 1 lần
    public void autoCleanPurchaseOrderTrash() {
        long currentTime = System.currentTimeMillis() / 1000;
        long duration = 2L * 60;
        int warningMinutes = 1;
        Long warningThreshold = currentTime - (duration - (warningMinutes * 60L));
        long deleteThreshold = currentTime - duration;
        cleanTrash(iPurchaseOrderRepository, 
            PurchaseSpecification.warningThreshold(warningThreshold), 
            PurchaseSpecification.deleteThreshold(deleteThreshold), 
            warningMinutes, 
            "PURCHASE ORDER", 
            null);
    }

    @Override
    public PagingResponse<PurchaseOrderResponse> getAllPurchaseOrderTrash(int page, int limit, String sortBy,
            String direction, PurchaseOrderFilterRequest filter) {
        return getAll(
                    page, 
                    limit, 
                    sortBy, 
                    direction, 
                    PurchaseSpecification.getAllPurchaseOrderTrashFilter(filter), 
                    PurchaseOrderResponse.class, 
                    iPurchaseOrderRepository);
    }

    @Override
    public APIResponse<PurchaseOrderDetailResponse> getPurchaseOrderTrashDetail(String id) {
        return getById(
                    UUID.fromString(id), 
                    iPurchaseOrderRepository, 
                    PurchaseOrder.class, 
                    PurchaseOrderDetailResponse.class);
    }

    @Override
    public APIResponse<Boolean> restorePurchaseOrder(String id, RestoreEnum action) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'restorePurchaseOrder'");
    }

    @Override
    @Transactional
    public APIResponse<Boolean> completePurchaseOrder(String poCode, String status, String note, String type) {

        PurchaseOrder purchaseOrder = iPurchaseOrderRepository.findByPoNumber(poCode).orElseThrow(() -> new IllegalArgumentException("Purchase order not found with code: " + poCode));

        if ("COMPLETED".equalsIgnoreCase(purchaseOrder.getStatus()) || "CANCELLED".equalsIgnoreCase(purchaseOrder.getStatus())) {
            throw new IllegalArgumentException("Purchase order is already completed or canceled.");
        }

        // Tạo delivery info
        PurchaseOrderDelivery delivery = PurchaseOrderDelivery.builder()
                    .actualDeliveryDate(LocalDate.now())
                    .deliveryNote(note)
                    .purchaseOrder(purchaseOrder)
                    .status(status)
                    .build();

        // CASE CANCEL
        if ("CANCELLED".equalsIgnoreCase(status)) {

            iPurchaseOrderDeliverryRepository.save(delivery);

            return new APIResponse<>(true, "Purchase order cancelled successfully.");
        }

        // CASE COMPLETE
        if ("COMPLETED".equalsIgnoreCase(status)) {

            for (PurchaseOrderItem item : purchaseOrder.getItems()) {

                Product product = item.getProduct();

                Inventory inventory = Inventory.builder()
                        .product(product)
                        .warehouse(purchaseOrder.getWarehouse())
                        .quantity(item.getQuantityOrdered())      
                        .type(type) 
                        .referenceCode(purchaseOrder.getPoNumber())
                        .build();

                inventory.setCode(randomCode());
                inventory.setInActive(true);
                inventory.setCreatedDate(new Date());
                inventory.setModifiedDate(new Date());
                inventory.setDeleted(false);
                inventory.setDeletedAt(0L);

                iInventoryRepository.save(inventory);
            }

            iPurchaseOrderDeliverryRepository.save(delivery);
            purchaseOrder.setPurchaseOrderDelivery(delivery);

            iPurchaseOrderRepository.save(purchaseOrder);
        }

        return new APIResponse<>(true, "Update status Purchase Order successfully.");
    } 
    
}
