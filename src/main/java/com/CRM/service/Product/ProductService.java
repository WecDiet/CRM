package com.CRM.service.Product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.CRM.model.Product;
import com.CRM.repository.IProductRepository;
import com.CRM.repository.Specification.Product.ProductSpecification;
import com.CRM.request.Product.ProductFilter;
import com.CRM.response.Pagination.PageResponse;
import com.CRM.response.Product.ProductDTO;
import com.CRM.service.HelperService.HelperService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService extends HelperService<Product, Long> implements IProductService {

    @Autowired
    private IProductRepository iProductRepository;

    @Override
    public PageResponse<ProductDTO> getAllProducts(int page, int limit, String sortBy, String direction,
            ProductFilter filter) {
        return getAll(
                page,
                limit,
                sortBy,
                direction,
                ProductSpecification.getAllProductFilter(filter),
                ProductDTO.class,
                iProductRepository);
    }

}