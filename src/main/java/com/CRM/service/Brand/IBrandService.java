package com.CRM.service.Brand;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.CRM.enums.RestoreEnum;
import com.CRM.request.Brand.BrandRequest;
import com.CRM.response.Brand.BrandResponse;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;
import com.CRM.response.Role.RoleResponse;

public interface IBrandService {
    PagingResponse<BrandResponse> getAllBrand(int page, int limit, String sortBy, String direction,
            String categotyName, boolean active);

    APIResponse<Boolean> createBrand(BrandRequest brandRequest, MultipartFile image, int width, int height);

    APIResponse<Boolean> updateBrand(String id, BrandRequest brandRequest, MultipartFile image, int width, int height);

    APIResponse<Boolean> deleteBrand(String id);

    PagingResponse<BrandResponse> getAllBrandTrash(int page, int limit, String sortBy, String direction);

    void autoCleanBrandTrash();

    APIResponse<Boolean> restoreBrand(String id, RestoreEnum action);
}
