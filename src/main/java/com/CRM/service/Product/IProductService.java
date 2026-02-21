package com.CRM.service.Product;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.CRM.request.Product.ProductFilter;
import com.CRM.request.Product.ProductRquest;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;
import com.CRM.response.Product.ProductDetailResponse;

public interface IProductService {
    PagingResponse<ProductDetailResponse> getAllProducts(
            int page, int limit, String sortBy, String direction,
            ProductFilter filter);

    APIResponse<ProductDetailResponse> getProductById(String id);

    APIResponse<Boolean> createProduct(ProductRquest productRquest, List<MultipartFile> medias);

}
