package com.CRM.service.Brand;

import org.springframework.web.multipart.MultipartFile;

import com.CRM.request.Brand.brandRequest;
import com.CRM.response.Brand.BrandResponse;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;

public interface IBrandService {
    PagingResponse<BrandResponse> getAllBrand(int page, int limit, String sortBy, String direction);

    APIResponse<Boolean> createBrand(brandRequest brandRequest, MultipartFile image, int width, int height);

    APIResponse<Boolean> updateBrand(Long id, brandRequest brandRequest, MultipartFile image, int width, int height);

    APIResponse<Boolean> deleteBrand(Long id);
}
