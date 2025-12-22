package com.CRM.service.Product;

import com.CRM.request.Product.ProductFilter;
import com.CRM.response.Pagination.PageResponse;
import com.CRM.response.Product.ProductDTO;

public interface IProductService {
    public PageResponse<ProductDTO> getAllProducts(
            int page, int limit, String sortBy, String direction,
            ProductFilter filter);
}
