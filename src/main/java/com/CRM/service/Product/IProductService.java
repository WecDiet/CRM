package com.CRM.service.Product;

import com.CRM.request.Product.ProductFilter;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;
import com.CRM.response.Product.ProductDetailResponse;

public interface IProductService {
    public PagingResponse<ProductDetailResponse> getAllProducts(
            int page, int limit, String sortBy, String direction,
            ProductFilter filter);

    public APIResponse<ProductDetailResponse> getProductById(Long id);
}
