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
import com.CRM.model.InventoryTransaction;
import com.CRM.model.Product;
import com.CRM.model.ProductDetail;
import com.CRM.model.PurchaseOrder;
import com.CRM.model.PurchaseOrderDelivery;
import com.CRM.model.PurchaseOrderDeliveryItem;
import com.CRM.model.PurchaseOrderItem;
import com.CRM.model.Store;
import com.CRM.model.Supplier;
import com.CRM.model.Warehouse;
import com.CRM.repository.IInventoryRepository;
import com.CRM.repository.IIventoryTransactionRepository;
import com.CRM.repository.IProductRepository;
import com.CRM.repository.IPurchaseOrderDeliverryRepository;
import com.CRM.repository.IPurchaseOrderRepository;
import com.CRM.repository.ISupplierRepository;
import com.CRM.repository.IWarehouseRepository;
import com.CRM.repository.Specification.PurchaseSpecification;
import com.CRM.request.PurchaseOrder.OrderItemRequest;
import com.CRM.request.PurchaseOrder.PurchaseOrderFilterRequest;
import com.CRM.request.PurchaseOrder.PurchaseOrderRequest;
import com.CRM.request.PurchaseOrder.Delivery.ReceiveDeliveryRequest;
import com.CRM.request.PurchaseOrder.Delivery.ReceivedItemRequest;
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

    private final IIventoryTransactionRepository iIventoryTransactionRepository;

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

            String date = LocalDate.now().toString().replace("-", "");
            PurchaseOrder purchaseOrder = modelMapper.map(purchaseOrderRequest, PurchaseOrder.class);
            purchaseOrder.setWarehouse(warehouse);
            purchaseOrder.setSupplier(supplier);
            purchaseOrder.setName(purchaseOrderRequest.getName());
            purchaseOrder.setStatus(PurchaseOrderEnum.fromString("DRAFT").getStatus());
            purchaseOrder.setOrderDate(orderDate);
            purchaseOrder.setExpectedDeliveryDate(expectedDeliveryDate);
            purchaseOrder.setPoNumber("PO-" + generateUniqueCode() + "-" + date);
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
            String uploadedPublicId = null;

            for(OrderItemRequest itemRequest : purchaseOrderRequest.getItems()){
                if (itemRequest.getUnitPrice() == null || itemRequest.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException(
                        "Unit price must be greater than 0 for item: "
                        + (itemRequest.getSkuCode() != null ? itemRequest.getSkuCode() : itemRequest.getProductName()));
                }
 
                if (itemRequest.getQuantityOrdered() == null || itemRequest.getQuantityOrdered() <= 0) {
                    throw new IllegalArgumentException(
                            "Quantity ordered must be greater than 0 for item: "
                            + (itemRequest.getSkuCode() != null ? itemRequest.getSkuCode() : itemRequest.getProductName()));
                }
                Product product = null;
                
                String skuCode = itemRequest.getSkuCode();

                if (skuCode != null && !skuCode.isBlank()) {

                    product = productMap.get(skuCode);
                    
                }else {

                    if (itemRequest.getProductName() == null || itemRequest.getProductName().isBlank()) {
                        throw new IllegalArgumentException("Product name is required when SKU code is not provided.");
                    }

                    List<Image> uploadedImages = new ArrayList<>();
                    if (images != null && !images.isEmpty()) {
                        for(MultipartFile file : images){
                           if (file == null || file.isEmpty()) continue;

                           try {

                                CompletableFuture<Map<String, Object>> uploadFuture = cloudinaryService.uploadMedia(file, "crm/products/main");

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
                                uploadedImages.add(imageProduct);
                           } catch (Exception e) {
                                throw new BadRequestException("Failed to upload image: " + file.getOriginalFilename());
                           }
                        }
                    }

                    Product newProduct = Product.builder()
                                    .skuCode(generateUniqueCode())
                                    .mainImage(uploadedImages.get(0))
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
                            .name(itemRequest.getProductName())
                            .manufacturer(supplier.getName())
                            .build();

                    newProduct.setProductDetail(productDetail);
                    newProducts.add(newProduct);
                    productMap.put(skuCode, newProduct);
                    product = newProduct;
                }

                PurchaseOrderItem orderItem = PurchaseOrderItem.builder()
                        .purchaseOrder(purchaseOrder)
                        .product(product)
                        .productName(itemRequest.getProductName())
                        .unitPrice(itemRequest.getUnitPrice())
                        .taxRate(itemRequest.getTaxRate())
                        .quantityOrdered(itemRequest.getQuantityOrdered())
                        .quantityReceived(0)
                        .build();
                orderItems.add(orderItem);
            }
             if (!newProducts.isEmpty()) {
                iProductRepository.saveAll(newProducts);
            }

            purchaseOrder.setItems(orderItems);

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

    
    // private BigDecimal calculateTotalAmount(OrderItemRequest totalAmount) {
    //     // 1. Lấy đơn giá nhập
    //     BigDecimal unitPrice = totalAmount.getUnitPrice();
        
    //     // 2. Chuyển đổi số lượng (Integer) sang BigDecimal để tính toán
    //     BigDecimal quantity = BigDecimal.valueOf(totalAmount.getQuantityOrdered());
        
    //     // 3. Xử lý thuế suất (Tax Rate)
    //     // Nếu taxRate là 0.1 (10%), công thức tính là: UnitPrice * Quantity * (1 + 0.1)
    //     double taxRate = (totalAmount.getTaxRate() != null) ? totalAmount.getTaxRate() : 0.0;
    //     BigDecimal taxMultiplier = BigDecimal.valueOf(1.0 + taxRate);

    //     // 4. Thực hiện phép nhân: (UnitPrice * Quantity) * (1 + TaxRate)
    //     return unitPrice.multiply(quantity).multiply(taxMultiplier);
    // }

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
            if ("CANCELLED".equalsIgnoreCase(purchaseOrder.getStatus()) || "COMPLETED".equalsIgnoreCase(purchaseOrder.getDeliveries().get(0).getStatus())) {
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
    public APIResponse<Boolean> confirmOrder(String id) {
        PurchaseOrder purchaseOrder = iPurchaseOrderRepository.findById(UUID.fromString(id)).orElseThrow(
            () -> new IllegalArgumentException("Purchase Order")
        );
        validateStatus(purchaseOrder,  "DRAFT", "Chỉ có thể xác nhận đơn hàng ở trạng thái DRAFT");

        BigDecimal total = purchaseOrder.getItems().stream()
            .map(item -> item.getUnitPrice()
                .multiply(BigDecimal.valueOf(item.getQuantityOrdered()))
                .multiply(BigDecimal.ONE.add(BigDecimal.valueOf(item.getTaxRate()))))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        purchaseOrder.setTotalAmount(total);
        purchaseOrder.setStatus("ORDERED");
        return new APIResponse<>(true, "Confirm Order from Purchase Order successfully.");
    }

    private void validateStatus(PurchaseOrder po, String expected, String message) {
        if (!expected.equals(po.getStatus())) {
            throw new IllegalArgumentException(message + ". Trạng thái hiện tại: " + po.getStatus());
        }
    }

    @Override
    @Transactional
    public APIResponse<Boolean> receiveDelivery(String poCode, ReceiveDeliveryRequest receiveDeliveryRequest, List<MultipartFile> images) {
        PurchaseOrder purchaseOrder = iPurchaseOrderRepository.findByPONumberWithItems(poCode).orElseThrow(
            () -> new IllegalArgumentException("Purchase Order not found"));

        if (!List.of("ORDERED", "PARTIALLY_RECEIVED").contains(purchaseOrder.getStatus())) {
            throw new IllegalArgumentException("Unable to receive goods for PO in this state: " + purchaseOrder.getStatus());
        }

        int deliveryNumber = purchaseOrder.getDeliveries().size() + 1;

        Map<UUID, PurchaseOrderItem> itemMap = purchaseOrder.getItems().stream()
                        .collect(Collectors.toMap(PurchaseOrderItem::getId, item -> item));
        
        PurchaseOrderDelivery delivery = PurchaseOrderDelivery.builder()
                            .purchaseOrder(purchaseOrder)
                            .deliveryNumber(deliveryNumber)
                            .actualDeliveryDate(LocalDate.now())
                            .status("COMPLETED")
                            .deliveryNote(receiveDeliveryRequest.getDeliveryNote())
                            .items(new ArrayList<>())
                            .build();

        List<Image> imageList = new ArrayList<>();
        String uploadedPublicId = null;
        if (images != null && !images.isEmpty()) {
            if (images.size() > 5) {
                    throw new IllegalArgumentException("You can upload a maximum of 5 images.");
            }

            for(MultipartFile file : images){
                if (!file.isEmpty()) {
                    CompletableFuture<Map<String, Object>> uploadFuture = cloudinaryService.uploadMedia(file, "crm/deliveries/PurchaseOrder");
                        Map<String, Object> uploadResult = uploadFuture.join();
                        uploadedPublicId = (String) uploadResult.get("public_id");
                        String mediaUrl = (String) uploadResult.get("secure_url");

                        Image image = Image.builder()
                                .imageUrl(mediaUrl)
                                .publicId(uploadedPublicId)
                                .referenceType("PURCHASE_ORDER_DELIVERY")
                                .referenceId(delivery.getId())
                                .altText(delivery.getDeliveryNumber().toString())
                                .type("IMAGE")
                                .build();

                        image.setInActive(true);
                        image.setCreatedDate(new Date());
                        image.setModifiedDate(new Date());
                        image.setCode(randomCode());
                        image.setDeletedAt(0L);
                        image.setDeleted(false);

                        imageList.add(image);
                    }
                }
        }

        if (!imageList.isEmpty()) {
            delivery.setImages(imageList);
        }
        for(ReceivedItemRequest request : receiveDeliveryRequest.getItems()){

            if (request.getQuantityDelivered() == null || request.getQuantityDelivered() <= 0) {
                throw new IllegalArgumentException(
                        "Quantity delivered must be greater than 0 for item: " + request.getPoItemId());
            }

            PurchaseOrderItem poItem = itemMap.get(UUID.fromString(request.getPoItemId()));
            if (poItem == null) {
                throw new IllegalArgumentException(
                        "PurchaseOrderItem not found in this PO: " + request.getPoItemId());
            }

            int remaining = poItem.getQuantityOrdered() - poItem.getQuantityReceived();
            
            if(request.getQuantityDelivered() > remaining){
                throw new IllegalArgumentException(String.format(
                        "Quantity delivered (%d) exceeds remaining quantity (%d) for product '%s'. "
                        + "Ordered: %d, Already received: %d",
                        request.getQuantityDelivered(),
                        remaining,
                        poItem.getProductName(),
                        poItem.getQuantityOrdered(),
                        poItem.getQuantityReceived()));
            }

            PurchaseOrderDeliveryItem deliveryItem = PurchaseOrderDeliveryItem.builder()
                                    .delivery(delivery)
                                    .purchaseOrderItem(poItem)
                                    .quantityDelivered(request.getQuantityDelivered())
                                    .build();
            delivery.getItems().add(deliveryItem);

            // Cập nhật lũy kế quantityReceived trong POItem
            poItem.setQuantityReceived(poItem.getQuantityReceived() + request.getQuantityDelivered());

            // Upsert Inventory Kho Tổng (PESSIMISTIC_WRITE)
            upsertWarehouseInventory(poItem.getProduct(), purchaseOrder.getWarehouse(), request.getQuantityDelivered());

            String referenceCode = purchaseOrder.getPoNumber() + "/ Delivery #" + deliveryNumber;

            insertInventoryTransaction(
                    poItem.getProduct(), 
                    purchaseOrder.getWarehouse(), 
                    null, 
                    "IN_PURCHASE", 
                    request.getQuantityDelivered(), 
                    purchaseOrder.getId(), 
                    referenceCode);
        }

        purchaseOrder.getDeliveries().add(delivery);

        // Cập nhật PO.status dựa trên lũy kế toàn bộ items
        boolean allCompleted = purchaseOrder.getItems().stream()
                .allMatch(i -> i.getQuantityReceived().equals(i.getQuantityOrdered()));
 
        purchaseOrder.setStatus(allCompleted ? "COMPLETED" : "PARTIALLY_RECEIVED");
        purchaseOrder.setModifiedDate(new Date());

        iPurchaseOrderRepository.save(purchaseOrder);

        String message = allCompleted
                ? "Delivery #" + deliveryNumber + " recorded. Purchase order is now COMPLETED."
                : "Delivery #" + deliveryNumber + " recorded. Purchase order is PARTIALLY_RECEIVED.";

        return new APIResponse<>(true, message);
    }

    /*
        Upsert Inventory tại Kho Tổng với PESSIMISTIC LOCK.
        Tạo mới nếu chưa có record cho product + warehouse.
    */
    private void upsertWarehouseInventory(Product product, Warehouse warehouse, int delta){
        Inventory inventory = iInventoryRepository.findByProductAndWarehouseWithLock(product, warehouse)
                                .orElseGet(() -> Inventory.builder()
                                                .product(product)
                                                .warehouse(warehouse)
                                                .store(null)
                                                .quantityOnHand(0)
                                                .build());
            inventory.setQuantityOnHand(inventory.getQuantityOnHand() + delta);
            inventory.setCode(randomCode());
            inventory.setInActive(true);
            inventory.setCreatedDate(new Date());
            inventory.setDeletedAt(0L);
            inventory.setModifiedDate(new Date());
            inventory.setDeleted(false);
            iInventoryRepository.save(inventory);

    }

    private void insertInventoryTransaction(Product product, Warehouse warehouse, Store store, String type, int quantity, UUID referenceId, String referenceCode){
        InventoryTransaction inventoryTransaction = InventoryTransaction.builder()
                                .product(product)
                                .warehouse(warehouse)
                                .store(store)
                                .type(type)
                                .quantity(quantity)
                                .referenceId(referenceId)
                                .referenceCode(referenceCode)
                                .build();
        iIventoryTransactionRepository.save(inventoryTransaction);
    }

}
