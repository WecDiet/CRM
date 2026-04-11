package com.CRM.service.PurchaseOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
import com.CRM.model.PatternColor;
import com.CRM.model.Product;
import com.CRM.model.ProductDetail;
import com.CRM.model.PurchaseOrder;
import com.CRM.model.PurchaseOrderColor;
import com.CRM.model.PurchaseOrderDelivery;
import com.CRM.model.PurchaseOrderDeliveryItem;
import com.CRM.model.PurchaseOrderItem;
import com.CRM.model.Supplier;
import com.CRM.model.Warehouse;
import com.CRM.repository.IInventoryRepository;
import com.CRM.repository.IInventoryTransactionRepository;
import com.CRM.repository.IPatternColorRepository;
import com.CRM.repository.IProductRepository;
import com.CRM.repository.IPurchaseOrderColorRepository;
import com.CRM.repository.IPurchaseOrderItemRepository;
import com.CRM.repository.IPurchaseOrderRepository;
import com.CRM.repository.ISupplierRepository;
import com.CRM.repository.IWarehouseRepository;
import com.CRM.repository.Specification.PurchaseSpecification;
import com.CRM.request.PurchaseOrder.OrderItemRequest;
import com.CRM.request.PurchaseOrder.PatternColorRequest;
import com.CRM.request.PurchaseOrder.PurchaseOrderFilterRequest;
import com.CRM.request.PurchaseOrder.PurchaseOrderRequest;
import com.CRM.request.PurchaseOrder.Delivery.ColorDeliveryDetailRequest;
import com.CRM.request.PurchaseOrder.Delivery.ReceiveDeliveryRequest;
import com.CRM.request.PurchaseOrder.Delivery.ReceivedItemRequest;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;
import com.CRM.response.PatternColor.PatternColorItem;
import com.CRM.response.PurchaseOrder.PurchaseOrderDetailResponse;
import com.CRM.response.PurchaseOrder.PurchaseOrderResponse;
import com.CRM.response.ReceiveDelivery.POReceiveColorResponse;
import com.CRM.response.ReceiveDelivery.POReceiveInfoResponse;
import com.CRM.response.ReceiveDelivery.POReceiveItemResponse;
import com.CRM.service.Cloudinary.CloudinaryService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService extends HelperService<PurchaseOrder, UUID> implements IPurchaseOrderService {

    private final IPurchaseOrderRepository iPurchaseOrderRepository;

    private final IPurchaseOrderItemRepository iPurchaseOrderItemRepository;

    private final IWarehouseRepository iWarehouseRepository;

    private final IProductRepository iProductRepository;

    private final ISupplierRepository iSupplierRepository;

    private final IInventoryRepository iInventoryRepository;

    private final CloudinaryService cloudinaryService;

    private final IInventoryTransactionRepository iIventoryTransactionRepository;

    private final IPatternColorRepository iPatternColorRepository;

    private final ModelMapper modelMapper;

    private final IPurchaseOrderColorRepository iPurchaseOrderColorRepository;

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

        if (purchaseOrderRequest.getName() == null || purchaseOrderRequest.getName().isBlank()){
            throw new IllegalArgumentException("Purchase order name cannot be empty.");
        }

        if (iPurchaseOrderRepository.existsActiveByName(purchaseOrderRequest.getName())){
            throw new IllegalArgumentException("Purchase order name already exists.");
        }

        List<OrderItemRequest> itemRequests = purchaseOrderRequest.getItems();
        if (itemRequests == null || itemRequests.isEmpty()){
            throw new IllegalArgumentException("Purchase order must have at least one item.");
        }

        Warehouse warehouse = iWarehouseRepository
            .findById(UUID.fromString(purchaseOrderRequest.getWarehouseId()))
            .orElseThrow(() -> new IllegalArgumentException(
                    "Warehouse not found: " + purchaseOrderRequest.getWarehouseId()));
 
        if (!"MAIN".equalsIgnoreCase(warehouse.getWarehouseType()))
            throw new IllegalArgumentException("The warehouse is not the main warehouse.");
    
        Supplier supplier = iSupplierRepository
                .findById(UUID.fromString(purchaseOrderRequest.getSupplierId()))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Supplier not found: " + purchaseOrderRequest.getSupplierId()));
 
        itemRequests.forEach(item ->{
            String label = item.getSkuCode() != null ? item.getSkuCode() : item.getProductName();
            if (item.getUnitPrice() == null || item.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0){
                throw new IllegalArgumentException("Unit price must be > 0 for item: " + label);
            }
    
            if ((item.getSkuCode() == null || item.getSkuCode().isBlank())
                    && (item.getProductName() == null || item.getProductName().isBlank()))
                throw new IllegalArgumentException("Product name is required when SKU code is not provided.");
        });

        // Load tất cả sản phẩm theo SKU
        List<String> skuCodes = itemRequests.stream()
                            .map(OrderItemRequest::getSkuCode)
                            .filter(sku -> sku != null && !sku.isBlank())
                            .collect(Collectors.toList());

        Map<String, Product> productMap = skuCodes.isEmpty() 
                        ? Collections.emptyMap()
                        : iProductRepository.findBySkuCodeIn(skuCodes).stream()
                                .collect(Collectors.toMap(Product::getSkuCode, product -> product));

        List<String> notFoundSkus = skuCodes.stream()
                            .filter(sku -> !productMap.containsKey(sku))
                            .collect(Collectors.toList());
                
        if (!notFoundSkus.isEmpty()){
            throw new IllegalArgumentException("Products not found with SKU: " + String.join(", ", notFoundSkus));
        }

        // Load tất cả PatternColor cần dùng
        Set<UUID> patternColorIds = itemRequests.stream()
                        .flatMap(item -> item.getColors().stream())
                        .map(PatternColorRequest::getPatternColorId)
                        .filter(Objects::nonNull)
                        .map(UUID::fromString)
                        .collect(Collectors.toSet());

        Map<UUID, PatternColor> patternColorMap = patternColorIds.isEmpty()
                                ? Collections.emptyMap()
                                : iPatternColorRepository.findAllById(patternColorIds).stream()
                                            .collect(Collectors.toMap(PatternColor::getId, color -> color));
        
        Set<UUID> notFoundColorIds = patternColorIds.stream()
                            .filter(color -> !patternColorMap.containsKey(color))
                            .collect(Collectors.toSet());
        
        if (!notFoundColorIds.isEmpty()){
            throw new IllegalArgumentException("PatternColor not found: "
                + notFoundColorIds.stream().map(UUID::toString).collect(Collectors.joining(", ")));
        }

        // Load PatternColor đã có trong DB cho sản phẩm mới
        // Mục đích: tránh INSERT trùng (lensColor + frameColor) đã tồn tại
        // Key = "lensColor|frameColor" (frameColor null → dùng "")
        // Set<String> newProductLensColors = itemRequests.stream()
        //         .filter(item -> item.getSkuCode() == null || item.getSkuCode().isBlank())
        //         .flatMap(item -> item.getColors().stream())
        //         .filter(cq -> cq.getPatternColorId() == null && cq.getLensColor() != null)
        //         .map(PatternColorRequest::getLensColor)
        //         .collect(Collectors.toSet());

        Set<String> colorKeys  = itemRequests.stream()
                .filter(item -> item.getSkuCode() == null || item.getSkuCode().isBlank())
                .flatMap(item -> item.getColors().stream())
                .filter(cq -> cq.getPatternColorId() == null && cq.getLensColor() != null)
                .map(cq -> cq.getLensColor() + "|" + (cq.getFrameColor() != null ? cq.getFrameColor() : ""))
                .collect(Collectors.toSet());
        
        
        Map<String, PatternColor> existingPatternColorMap = colorKeys.isEmpty()
            ? Collections.emptyMap()
            : iPatternColorRepository.findByLensFrameColorKeys(colorKeys).stream()
                    .collect(Collectors.toMap(
                            pc -> pc.getLensColor() + "|" + (pc.getFrameColor() != null ? pc.getFrameColor() : ""),
                            pc -> pc,
                            (a, b) -> a,
                            HashMap::new // dùng HashMap để có thể mutate -> add vào map khi tạo mới PatternColor nếu chưa có (trường hợp có nhiều màu mới cùng lensColor nhưng khác frameColor 
                        )); // Merge Function: Xử lý trường hợp bị trùng lặp Key. Nếu trong DB có 2 dòng lens và frame trùng nhau thì sẽ chọn thằng đầu tiên 

        
        AtomicInteger imageIndex = new AtomicInteger(0);
        Map<Integer, CompletableFuture<Image>> uploadFutureMap = new HashMap<>();
        
        for (int i = 0; i < itemRequests.size(); i++) {
        if (itemRequests.get(i).getSkuCode() == null || itemRequests.get(i).getSkuCode().isBlank()) {
            int idx = imageIndex.getAndIncrement();
            if (images != null && idx < images.size()) {
                final MultipartFile file = images.get(idx);
                final String productName = itemRequests.get(i).getProductName();
                uploadFutureMap.put(i, cloudinaryService.uploadImage(file, "crm/products/main")
                        .thenApply(result -> {
                            Image image = Image.builder()
                                    .imageUrl((String) result.get("secure_url"))
                                    .publicId((String) result.get("public_id"))
                                    .referenceId(supplier.getId())
                                    .referenceType("PRODUCT")
                                    .altText(productName)
                                    .type("IMAGE")
                                    .build();
                            image.setInActive(true);
                            image.setCode(randomCode());
                            image.setCreatedDate(new Date());
                            image.setModifiedDate(new Date());
                            image.setDeleted(false);
                            image.setDeletedAt(0L);
                            return image;
                        }));
                }
            }
        }

        if (!uploadFutureMap.isEmpty()) {
            try {
                CompletableFuture.allOf(uploadFutureMap.values().toArray(new CompletableFuture[0])).join();
            } catch (Exception e) {
                throw new BadRequestException("Failed to upload one or more product images.");
            }
        }

        String date = LocalDate.now().toString().replace("-", "");
        PurchaseOrder purchaseOrder = PurchaseOrder.builder()
                .name(purchaseOrderRequest.getName())
                .poNumber("PO-" + generateUniqueCode() + "-" + date)
                .supplier(supplier)
                .warehouse(warehouse)
                .status(PurchaseOrderEnum.fromString("DRAFT").getStatus())
                .orderDate(purchaseOrderRequest.getOrderDate())
                .expectedDeliveryDate(purchaseOrderRequest.getExpectedDeliveryDate())
                .note(purchaseOrderRequest.getNote())
                .items(new HashSet<>())
                .deliveries(new ArrayList<>())
                .build();
    
        purchaseOrder.setInActive(active);
        purchaseOrder.setCode(randomCode());
        purchaseOrder.setCreatedDate(new Date());
        purchaseOrder.setModifiedDate(new Date());
        purchaseOrder.setDeleted(false);
        purchaseOrder.setDeletedAt(0L);

        List<Product> newProducts = new ArrayList<>();

        Set<PurchaseOrderItem> orderItems = IntStream.range(0, itemRequests.size())
                        .mapToObj(i -> {
                            OrderItemRequest item = itemRequests.get(i);

                            boolean hasSkuCode = item.getSkuCode() != null && !item.getSkuCode().isBlank();

                            Product product = hasSkuCode 
                                    ? resolveExistingProduct(item, productMap)
                                    : createNewProduct(item, supplier, active, 
                                       uploadFutureMap.containsKey(i) ? uploadFutureMap.get(i).join() : null,
                                        newProducts, existingPatternColorMap);
                            
                            Set<PurchaseOrderColor> colorItems = item.getColors().stream()
                                                .map(cq -> {
                                                    PatternColor patternColor = cq.getPatternColorId() != null
                                                        // Sản phẩm cũ: lấy từ map đã batch load theo ID
                                                        ? patternColorMap.get(UUID.fromString(cq.getPatternColorId()))
                                                        // Sản phẩm mới: lấy từ ProductDetail (đã reuse hoặc tạo mới)
                                                        : (!hasSkuCode ? matchNewProductColor(product, cq) : null);


                                                    return PurchaseOrderColor.builder()
                                                            .patternColor(patternColor)
                                                            .quantityOrdered(cq.getQuantityOrdered())
                                                            .quantityReceived(0)
                                                            .build();

                                                }).collect(Collectors.toSet());
                                
                            PurchaseOrderItem purchaseOrderItem = PurchaseOrderItem.builder()
                                                .purchaseOrder(purchaseOrder)
                                                .product(product)
                                                .productName(hasSkuCode ? product.getProductDetail().getName() : item.getProductName())
                                                .unitPrice(item.getUnitPrice())
                                                .taxRate(item.getTaxRate() != null ? item.getTaxRate() : 0.0)
                                                .colorDetails(colorItems)
                                                .build();

                                colorItems.forEach(c -> c.setPurchaseOrderItem(purchaseOrderItem));
                                
                                return purchaseOrderItem;

                        }).collect(Collectors.toSet());
                    
                if (!newProducts.isEmpty()) {
                    // Collect tất cả PatternColor mới (chưa có ID) cần save
                    List<PatternColor> newPatternColors = existingPatternColorMap.values().stream()
                            .filter(pc -> pc.getId() == null) // chưa có ID = chưa được persist
                            .collect(Collectors.toList());

                    if (!newPatternColors.isEmpty()) {
                        iPatternColorRepository.saveAll(newPatternColors);
                    }

                    iProductRepository.saveAll(newProducts); // sau đó mới save Product + ProductDetail
                }
                purchaseOrder.setItems(orderItems);
                iPurchaseOrderRepository.save(purchaseOrder);         // cascade → items → colorItems
        return new APIResponse<>(true, "Purchase order created successfully.");
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
            if (List.of("DRAFT", "CANCELLED").contains(purchaseOrder.getStatus())) {


                List<Product> productsToCheck = purchaseOrder.getItems().stream()
                    .map(PurchaseOrderItem::getProduct)
                    .filter(product ->{
                        long refCount = iPurchaseOrderItemRepository.countByProductAndPurchaseOrder(product.getId(), purchaseOrder.getId());
                        return refCount == 0; 
                    }).collect(Collectors.toList());
                
                if (!productsToCheck.isEmpty()) {

                    // Collect tất cả PatternColor của các sản phẩm bị xóa để kiểm tra có còn được tham chiếu ở đâu khác không trước khi xóa
                    List<PatternColor> colorsOfDeletedProducts = productsToCheck.stream()
                        .flatMap(product -> product.getProductDetail().getColors().stream())
                        .distinct()
                        .collect(Collectors.toList());
                    
                    // Lấy ID của các product bị xóa để loại khỏi check
                    Set<UUID> deletedProductDetailIds = productsToCheck.stream()
                        .map(product -> product.getProductDetail().getId())
                        .collect(Collectors.toSet());

                    // Chỉ xóa màu nào không còn ProductDetail nào khác dùng nữa
                    List<PatternColor> colorsToDelete = colorsOfDeletedProducts.stream()
                        .filter(pc -> {
                            long usageCount = iPatternColorRepository.countByColorIdExcludingProductDetails(pc.getId(), deletedProductDetailIds);
                            return usageCount == 0; // Nếu không còn ProductDetail nào khác dùng màu này, thì mới xóa
                        }).collect(Collectors.toList());
                    

                    productsToCheck.forEach(product -> product.getProductDetail().getColors().clear());

                    // Flush clear colors trước (xóa rows trong product_detail_colors)
                    iProductRepository.saveAll(productsToCheck);

                    // Sau đó mới xóa PatternColor orphan
                    if (!colorsToDelete.isEmpty()) {
                        Set<UUID> colorIdsToDelete = colorsToDelete.stream()
                            .map(PatternColor::getId)
                            .collect(Collectors.toSet());

                    // Bước 2: xóa PurchaseOrderColor liên quan → xóa rows po_item_colors
                    // trước khi xóa PatternColor để tránh FK violation
                    iPurchaseOrderColorRepository.deleteByPatternColorIdIn(colorIdsToDelete);

                    // Bước 3: xóa PatternColor orphan
                    iPatternColorRepository.deleteAllById(colorIdsToDelete);
                    }

                    for (Product product : productsToCheck) {
                        product.setInActive(false);
                        product.setDeleted(true);
                        product.setDeletedAt(System.currentTimeMillis() / 1000);
                    }

                    iProductRepository.saveAll(productsToCheck);
                }
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
        PurchaseOrder purchaseOrder = iPurchaseOrderRepository
                .findByPONumberWithItems(poCode)
                .orElseThrow(() -> new IllegalArgumentException("Purchase Order not found: " + poCode));

        if (!List.of("ORDERED", "PARTIALLY_RECEIVED").contains(purchaseOrder.getStatus()))
            throw new IllegalArgumentException(
                    "Unable to receive goods for PO in this state: " + purchaseOrder.getStatus());

        // ── BATCH 1: Map skuCode → PurchaseOrderItem ─────────────────────────
        Map<String, PurchaseOrderItem> itemMap = purchaseOrder.getItems().stream()
                .collect(Collectors.toMap(item -> item.getProduct().getSkuCode(), item -> item));

        // ── BATCH 2: Map colorId → PurchaseOrderColor ─────────────────────────
        Map<UUID, PurchaseOrderColor> colorMap = purchaseOrder.getItems().stream()
                .flatMap(poItem -> poItem.getColorDetails().stream())
                .collect(Collectors.toMap(PurchaseOrderColor::getId, color -> color));

        // ── VALIDATE TOÀN BỘ TRƯỚC — không mutate gì cho đến khi pass hết ────
        receiveDeliveryRequest.getItems().forEach(req -> {

            // Validate skuCode tồn tại trong PO
            PurchaseOrderItem poItem = itemMap.get(req.getSkuCode());
            if (poItem == null)
                throw new IllegalArgumentException(
                        "PurchaseOrderItem not found in this PO: " + req.getSkuCode());

            // Validate từng màu trong item
            req.getColors().forEach(colorReq -> {
                if (colorReq.getQuantityDelivered() == null || colorReq.getQuantityDelivered() <= 0)
                    throw new IllegalArgumentException(
                            "Số lượng giao phải > 0 cho màu: " + colorReq.getPoItemColorId());

                UUID colorId = UUID.fromString(colorReq.getPoItemColorId());
                PurchaseOrderColor colorItem = colorMap.get(colorId);

                if (colorItem == null)
                    throw new IllegalArgumentException(
                            "PurchaseOrderColor not found: " + colorReq.getPoItemColorId());

                // Đảm bảo màu thuộc đúng item được khai báo
                if (!colorItem.getPurchaseOrderItem().getProduct().getSkuCode().equals(req.getSkuCode()))
                    throw new IllegalArgumentException(
                            "Màu [" + colorReq.getPoItemColorId() + "] không thuộc sản phẩm [" + req.getSkuCode() + "].");

                int remaining = colorItem.getRemainingQuantity();
                if (colorReq.getQuantityDelivered() > remaining)
                    throw new IllegalArgumentException(String.format(
                            "Số lượng giao (%d) vượt quá số còn lại (%d) cho sản phẩm '%s' màu '%s'. Đặt: %d, Đã nhận: %d",
                            colorReq.getQuantityDelivered(),
                            remaining,
                            poItem.getProductName(),
                            colorItem.getPatternColor() != null
                                    ? colorItem.getPatternColor().getLensColorName() : "Không màu",
                            colorItem.getQuantityOrdered(),
                            colorItem.getQuantityReceived()));
            });
        });

        // ── Upload ảnh song song ──────────────────────────────────────────────
        List<String> uploadedPublicIds = new ArrayList<>();
        List<Image> imageList = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            if (images.size() > 5)
                throw new IllegalArgumentException("You can upload a maximum of 5 images.");

            List<CompletableFuture<Image>> uploadFutures = images.stream()
                    .filter(file -> !file.isEmpty())
                    .map(file -> cloudinaryService.uploadImage(file, "crm/deliveries/PurchaseOrder")
                            .thenApply(result -> {
                                uploadedPublicIds.add((String) result.get("public_id"));
                                Image image = Image.builder()
                                        .imageUrl((String) result.get("secure_url"))
                                        .publicId((String) result.get("public_id"))
                                        .referenceType("PURCHASE_ORDER_DELIVERY")
                                        .referenceId(null)
                                        .altText("Delivery #" + (purchaseOrder.getDeliveries().size() + 1))
                                        .type("IMAGE")
                                        .build();
                                image.setInActive(true);
                                image.setCode(randomCode());
                                image.setCreatedDate(new Date());
                                image.setModifiedDate(new Date());
                                image.setDeletedAt(0L);
                                image.setDeleted(false);
                                return image;
                            }))
                    .collect(Collectors.toList());
            try {
                CompletableFuture.allOf(uploadFutures.toArray(new CompletableFuture[0])).join();
                uploadFutures.forEach(f -> imageList.add(f.join()));
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to upload delivery images.");
            }
        }

        int deliveryNumber = purchaseOrder.getDeliveries().size() + 1;
        String referenceCode = purchaseOrder.getPoNumber() + " / Delivery #" + deliveryNumber;

        PurchaseOrderDelivery delivery = PurchaseOrderDelivery.builder()
                .purchaseOrder(purchaseOrder)
                .deliveryNumber(deliveryNumber)
                .actualDeliveryDate(LocalDate.now())
                .status("COMPLETED")
                .items(new ArrayList<>())
                .images(imageList)
                .build();

        // ── BATCH 3: Load Inventory — 1 query ────────────────────────────────
        Set<UUID> productIds = receiveDeliveryRequest.getItems().stream()
                .map(req -> itemMap.get(req.getSkuCode()).getProduct().getId())
                .collect(Collectors.toSet());

        Warehouse warehouse = purchaseOrder.getWarehouse();

        Map<UUID, Inventory> inventoryMap = iInventoryRepository
                .findAllByWarehouseIdAndProductIdIn(warehouse.getId(), productIds).stream()
                .collect(Collectors.toMap(inv -> inv.getProduct().getId(), inv -> inv));

        // ── Build delivery items: duyệt theo poItemId → colors ───────────────
        List<InventoryTransaction> transactions = new ArrayList<>();
        List<PurchaseOrderDeliveryItem> deliveryItems = new ArrayList<>();

        for (ReceivedItemRequest req : receiveDeliveryRequest.getItems()) {
            
            PurchaseOrderItem poItem = itemMap.get(req.getSkuCode());
            Product product = poItem.getProduct();

            for (ColorDeliveryDetailRequest colorReq : req.getColors()) {
                PurchaseOrderColor colorItem = colorMap.get(UUID.fromString(colorReq.getPoItemColorId()));

                // Cập nhật lũy kế theo màu
                colorItem.setQuantityReceived(colorItem.getQuantityReceived() + colorReq.getQuantityDelivered());

                // Upsert Inventory từ map — không query DB
                Inventory inventory = inventoryMap.computeIfAbsent(product.getId(), id -> {
                    Inventory newInventory = Inventory.builder()
                            .product(product)
                            .warehouse(warehouse)
                            .store(null)
                            .quantityOnHand(0)
                            .build();
                    newInventory.setInActive(true);
                    newInventory.setCode(randomCode());
                    newInventory.setCreatedDate(new Date());
                    newInventory.setModifiedDate(new Date());
                    newInventory.setDeleted(false);
                    newInventory.setDeletedAt(0L);
                    return newInventory;
                });
                inventory.setQuantityOnHand(inventory.getQuantityOnHand() + colorReq.getQuantityDelivered());
                inventory.setModifiedDate(new Date());

                InventoryTransaction transaction = InventoryTransaction.builder()
                        .product(product)
                        .warehouse(warehouse)
                        .store(null)
                        .type("IN_PURCHASE")
                        .quantity(colorReq.getQuantityDelivered())
                        .referenceId(purchaseOrder.getId())
                        .referenceCode(referenceCode)
                        .note(receiveDeliveryRequest.getDeliveryNote())
                        .build();
                transaction.setInActive(true);
                transaction.setCode(randomCode());
                transaction.setDeleted(false);
                transaction.setDeletedAt(0L);
                transaction.setCreatedDate(new Date());
                transaction.setModifiedDate(new Date());
                transactions.add(transaction);

                deliveryItems.add(PurchaseOrderDeliveryItem.builder()
                        .delivery(delivery)
                        .purchaseOrderItem(poItem)
                        .purchaseOrderColor(colorItem)
                        .quantityDelivered(colorReq.getQuantityDelivered())
                        .build());
            }
        }

        delivery.setItems(deliveryItems);

        // ── Tự động tính deliveryNote: "ĐỦ" hoặc "THIẾU: ..." ───────────────
        List<String> missingList = purchaseOrder.getItems().stream()
                .flatMap(poItem -> poItem.getColorDetails().stream()
                        .filter(c -> !c.isFullyReceived())
                        .map(c -> {
                            String colorName = c.getPatternColor() != null
                                    ? c.getPatternColor().getLensColorName()
                                    + (c.getPatternColor().getFrameColorName() != null
                                            ? " / " + c.getPatternColor().getFrameColorName() : "")
                                    : "Không màu";
                            return String.format("%s [%s]: còn thiếu %d",
                                    poItem.getProductName(), colorName, c.getRemainingQuantity());
                        }))
                .collect(Collectors.toList());

        String deliveryNote = missingList.isEmpty()
                ? "FULL"
                : "MISS:\n" + String.join("\n", missingList);

        if (receiveDeliveryRequest.getDeliveryNote() != null && !receiveDeliveryRequest.getDeliveryNote().isBlank())
            deliveryNote += "\nNote: " + receiveDeliveryRequest.getDeliveryNote();

        delivery.setDeliveryNote(deliveryNote);

        // ── Cập nhật trạng thái PO ────────────────────────────────────────────
        boolean allCompleted = missingList.isEmpty();
        purchaseOrder.setStatus(allCompleted ? "COMPLETED" : "PARTIALLY_RECEIVED");
        purchaseOrder.setModifiedDate(new Date());
        purchaseOrder.getDeliveries().add(delivery);

        // ── BATCH SAVE — 3 lần ghi cố định ───────────────────────────────────
        try {
            iInventoryRepository.saveAll(inventoryMap.values());
            iIventoryTransactionRepository.saveAll(transactions);
            iPurchaseOrderRepository.save(purchaseOrder);
        } catch (Exception e) {
            // DB fail → xóa ảnh đã upload tránh orphaned images
            uploadedPublicIds.forEach(publicId -> cloudinaryService.deleteImage(publicId));
            throw e;
        }

        String message = allCompleted
                ? "Delivery #" + deliveryNumber + " recorded. Purchase order is now COMPLETED."
                : "Delivery #" + deliveryNumber + " recorded. Purchase order is PARTIALLY_RECEIVED.";

        return new APIResponse<>(true, message);
    }


    
    // resolve sản phẩm cũ + validate màu dùng map có sẵn 
    private Product resolveExistingProduct(
            OrderItemRequest item,
            Map<String, Product> productMap
            ) {
    
        Product product = productMap.get(item.getSkuCode());
        Set<UUID> existingColorIds = product.getProductDetail().getColors().stream()
                .map(PatternColor::getId)
                .collect(Collectors.toSet());
    
        List<String> invalidColors = item.getColors().stream()
                .map(PatternColorRequest::getPatternColorId)
                .filter(id -> id != null && !existingColorIds.contains(UUID.fromString(id)))
                .collect(Collectors.toList());
    
        if (!invalidColors.isEmpty())
            throw new IllegalArgumentException(
                    "Màu không thuộc sản phẩm [" + item.getSkuCode() + "]: "
                            + String.join(", ", invalidColors));
        return product;
    }


    private Product createNewProduct(
        OrderItemRequest item,
        Supplier supplier,
        boolean active,
        Image mainImage,
        List<Product> newProducts,
        Map<String, PatternColor> existingPatternColorMap
    ){
        // Với mỗi màu trong request:
        //  - Nếu cặp (lensColor|frameColor) đã có trong DB → reuse, không INSERT mới
        //  - Nếu chưa có → tạo mới
        List<PatternColor> patternColors = item.getColors().stream()
                .filter(cq -> cq.getPatternColorId() == null && cq.getLensColor() != null)
                .map(cq -> {
                    String key = cq.getLensColor() + "|"
                            + (cq.getFrameColor() != null ? cq.getFrameColor() : "");

                    if (existingPatternColorMap.containsKey(key)) {
                        return existingPatternColorMap.get(key);
                    }
                    
                    // return existingPatternColorMap.getOrDefault(key,
                    //         PatternColor.builder()
                    //                 .lensColor(cq.getLensColor())
                    //                 .lensColorName(cq.getLensColorName())
                    //                 .frameColor(cq.getFrameColor())
                    //                 .frameColorName(cq.getFrameColorName())
                    //                 .build());

                    PatternColor patternColorNew = PatternColor.builder()
                            .lensColor(cq.getLensColor())
                            .lensColorName(cq.getLensColorName())
                            .frameColor(cq.getFrameColor())
                            .frameColorName(cq.getFrameColorName())
                            .build();

                    
                    existingPatternColorMap.put(key, patternColorNew); // ← cache để item sau reuse
                    return patternColorNew;
                })
                .collect(Collectors.toList());
    
        Product newProduct = Product.builder()
                .skuCode(generateUniqueCode())
                .mainImage(mainImage)
                .status(false)
                .build();
        newProduct.setInActive(active);
        newProduct.setCode(randomCode());
        newProduct.setCreatedDate(new Date());
        newProduct.setModifiedDate(new Date());
        newProduct.setDeleted(false);
        newProduct.setDeletedAt(0L);
        newProduct.setProductDetail(ProductDetail.builder()
                .product(newProduct)
                .name(item.getProductName())
                .manufacturer(supplier.getName())
                .colors(patternColors)
                .build());
    
        newProducts.add(newProduct);
        return newProduct;
    }

    private PatternColor matchNewProductColor(Product product, PatternColorRequest patternColorRequest) {
        return product.getProductDetail().getColors().stream()
            .filter(pc -> Objects.equals(pc.getLensColor(), patternColorRequest.getLensColor())
                    && Objects.equals(pc.getFrameColor(), patternColorRequest.getFrameColor()))
            .findFirst()
            .orElse(null);
    }

    @Override
    public APIResponse<POReceiveInfoResponse> getPOReceiveInfo(String poCode) {
        PurchaseOrder purchaseOrder = iPurchaseOrderRepository
                .findByPONumberWithItems(poCode)
                .orElseThrow(() -> new IllegalArgumentException("Purchase Order not found: " + poCode));
        
        if (!List.of("ORDERED", "PARTIALLY_RECEIVED").contains(purchaseOrder.getStatus())){
            throw new IllegalArgumentException(
                    "The Purchase Order (PO) is not in a ready-to-receive state. Current status: " + purchaseOrder.getStatus());
        }

        POReceiveInfoResponse response = modelMapper.map(purchaseOrder, POReceiveInfoResponse.class);
        response.setItems(purchaseOrder.getItems().stream()
                .map(this::mapToPOReceiveItemResponse)
                .collect(Collectors.toList()));

        return new APIResponse<>(response, "PO receive info retrieved successfully.");
    }


    private POReceiveItemResponse mapToPOReceiveItemResponse(PurchaseOrderItem item) {
        POReceiveItemResponse response = modelMapper.map(item, POReceiveItemResponse.class);

        response.setColors(item.getColorDetails().stream()
                .map(this::mapToPOReceiveColorResponse)
                .collect(Collectors.toList()));
        return response;
    }


    private POReceiveColorResponse mapToPOReceiveColorResponse(PurchaseOrderColor color) {
        POReceiveColorResponse response = modelMapper.map(color, POReceiveColorResponse.class);

        if (color.getPatternColor() != null) {
            response.setPatternColor(modelMapper.map(color.getPatternColor(), PatternColorItem.class));
        }
        return response;
    }
}
