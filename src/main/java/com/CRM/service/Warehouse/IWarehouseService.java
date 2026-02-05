package com.CRM.service.Warehouse;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.CRM.enums.RestoreEnum;
import com.CRM.request.Warehouse.WarehouseRequest;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;
import com.CRM.response.Warehouse.WarehouseResponse;

public interface IWarehouseService {
        PagingResponse<WarehouseResponse> getAllWarehouses(int page, int limit, String sortBy, String direction,
                        boolean active, WarehouseRequest filter);

        APIResponse<Boolean> createWarehouse(WarehouseRequest warehouseRequest, boolean active,
                        List<MultipartFile> images, int width,
                        int height);

        APIResponse<Boolean> updateWarehouse(String id, boolean active, WarehouseRequest warehouseRequest,
                        List<MultipartFile> images,
                        int width,
                        int height);

        APIResponse<Boolean> deleteWarehouse(String id);

        PagingResponse<WarehouseResponse> getAllWarehouseTrash(int page, int limit, String sortBy, String direction,
                        WarehouseRequest filter);

        void autoCleanWarehouseTrash();

        APIResponse<Boolean> restoreWarehouse(String id, RestoreEnum action);

}
