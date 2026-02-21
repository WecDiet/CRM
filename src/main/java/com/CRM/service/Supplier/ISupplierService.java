package com.CRM.service.Supplier;

import org.apache.coyote.BadRequestException;
import org.springframework.web.multipart.MultipartFile;

import com.CRM.request.Supplier.SupplierFilterRequest;
import com.CRM.request.Supplier.SupplierRequest;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;
import com.CRM.response.Supplier.SupplierResponse;
import com.CRM.response.Supplier.SupplierDetailResponse;

public interface ISupplierService {
    PagingResponse<SupplierResponse> getAllSupplier(int page, int limit, String sortBy, String direction, boolean active, SupplierFilterRequest filter);

    APIResponse<SupplierDetailResponse> getSupplierDetail(String id);
    
    APIResponse<Boolean> createSupplier(SupplierRequest supplierRequest, MultipartFile image, boolean active) throws BadRequestException;

    APIResponse<Boolean> updateSupplier(String id, SupplierRequest supplierRequest, MultipartFile image, boolean active);

    APIResponse<Boolean> deleteSupplier(String id);

    void autoCleanSupplierTrash();

    PagingResponse<SupplierResponse> getAllSupplierTrash(int page, int limit, String sortBy, String direction, SupplierFilterRequest filter);


}
