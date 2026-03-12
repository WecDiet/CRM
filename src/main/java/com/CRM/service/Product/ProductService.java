package com.CRM.service.Product;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.CRM.Util.Helper.HelperService;
import com.CRM.model.Image;
import com.CRM.model.Product;
import com.CRM.repository.IInventoryRepository;
import com.CRM.repository.IMediaRepository;
import com.CRM.repository.IProductRepository;
import com.CRM.repository.IPurchaseOrderItemRepository;
import com.CRM.repository.Specification.ProductSpecification;
import com.CRM.request.Inventory.InventoryFilterRequest;
import com.CRM.request.Product.ProductFilter;
import com.CRM.request.Product.ProductRquest;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;
import com.CRM.response.Product.ProductDetailResponse;
import com.CRM.response.Product.ProductResponse;
import com.CRM.response.Product.Inventory.InventoryProduct;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService extends HelperService<Product, UUID> implements IProductService {

    private final IProductRepository iProductRepository;

    private final IMediaRepository iMediaRepository;

    private final IInventoryRepository iInventoryRepository;

    private final IPurchaseOrderItemRepository iPurchaseOrderItemRepository;

    @Override
    public PagingResponse<ProductResponse> getAllProducts(int page, int limit, String sortBy, String direction, boolean active, ProductFilter filter) {
        return getAll(
                page,
                limit,
                sortBy,
                direction,
                ProductSpecification.getAllProductFilter(filter, active),
                ProductResponse.class,  
                iProductRepository);
    }

    @Override
    public APIResponse<ProductDetailResponse> getProductDetail(String id) {
        return getById(
                UUID.fromString(id),
                iProductRepository,
                Product.class,
                ProductDetailResponse.class);
    }

    @Override
    public APIResponse<Boolean> createProduct(ProductRquest productRquest, List<MultipartFile> medias) {

       return null;
    }

    @Override
    @Transactional
    public APIResponse<Boolean> deleteProduct(String id) {
        Product product = iProductRepository.findById(UUID.fromString(id)).orElseThrow(() -> {
            return new RuntimeException("Product not found");
        });

        if (iInventoryRepository.existsByProduct_Id(UUID.fromString(id))) {
            throw new RuntimeException("Product still exists in inventory. Cannot delete");
        }

        if (iPurchaseOrderItemRepository.existsByProduct_Id(UUID.fromString(id))) {
            throw new RuntimeException("Product is used in Purchase Order Item. Cannot delete");
        }

        List<Image> productDetailImages = product.getProductDetail().getImages();
        if (productDetailImages == null || productDetailImages.isEmpty()) {
            product.getProductDetail().getImages().stream().forEach(images ->{
                images.setInActive(false);
                images.setDeleted(true);
                images.setDeletedAt(System.currentTimeMillis() / 1000);
                iMediaRepository.save(images);
            });
        }

        product.setInActive(false);
        product.setDeleted(true);
        product.setDeletedAt(System.currentTimeMillis() / 1000);
        iProductRepository.save(product);

        return new APIResponse<>(true, "Delete product successfully");
    }

    @Override
    @Transactional
    @Scheduled(fixedRate = 60 * 1000) // Quét mõi 1 phút / 1 lần
    public void autoCleanProductTrash() {
        long currentTime = System.currentTimeMillis() / 1000;
        long duration = 2L * 60;
        int warningMinutes = 1;
        Long warningThreshold = currentTime - (duration - (warningMinutes * 60L));
        long deleteThreshold = currentTime - duration;

        cleanTrash(iProductRepository,
            ProductSpecification.warningThreshold(warningThreshold), 
            ProductSpecification.deleteThreshold(deleteThreshold),
            warningMinutes, 
            "PRODUCT", 
            null);
    }

    @Override
    public PagingResponse<ProductResponse> getAllProductTrash(int page, int limit, String sortBy, String direction,
            ProductFilter filter) {
        return getAll(
                page,
                limit,
                sortBy,
                direction,
                ProductSpecification.getAllProductTrashFilter(filter),
                ProductResponse.class,  
                iProductRepository);
    }

    @Override
    public APIResponse<ProductDetailResponse> getProductTrashDetail(String id) {
        return getById(
                UUID.fromString(id),
                iProductRepository,
                Product.class,
                ProductDetailResponse.class);
    }

    @Override
    public PagingResponse<InventoryProduct> getAllProductInventoty(int page, int limit, String sortBy, String direction,
            InventoryFilterRequest filter) {
        return getAll(
                    page, 
                    limit, 
                    sortBy, 
                    direction, 
                    ProductSpecification.getAllProductInInventory(filter), 
                    InventoryProduct.class, 
                    iProductRepository);
    }
}