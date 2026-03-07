package com.CRM.service.Product;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.CRM.request.Inventory.InventoryFilterRequest;
import com.CRM.request.Product.ProductFilter;
import com.CRM.request.Product.ProductRquest;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;
import com.CRM.response.Product.ProductDetailResponse;
import com.CRM.response.Product.ProductResponse;
import com.CRM.response.Product.Inventory.InventoryProduct;

public interface IProductService {
    PagingResponse<ProductResponse> getAllProducts(
            int page, int limit, String sortBy, String direction, boolean active, ProductFilter filter);

    APIResponse<ProductDetailResponse> getProductDetail(String id);

    APIResponse<Boolean> createProduct(ProductRquest productRquest, List<MultipartFile> medias);

    APIResponse<Boolean> deleteProduct(String id);

    void autoCleanProductTrash();

    PagingResponse<ProductResponse> getAllProductTrash(int page, int limit, String sortBy, String direction, ProductFilter filter);

    APIResponse<ProductDetailResponse> getProductTrashDetail(String id);

    PagingResponse<InventoryProduct> getAllProductInventoty(int page, int limit, String sortBy, String direction, InventoryFilterRequest filter);
}
