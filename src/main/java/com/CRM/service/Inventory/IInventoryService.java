package com.CRM.service.Inventory;

import com.CRM.request.Inventory.InventoryFilterRequest;
import com.CRM.response.Inventory.InventoryResponse;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;

public interface IInventoryService {

    PagingResponse<InventoryResponse> getAllInventories(int page, int limit, String sortBy, String direction);

    // APIResponse<Boolean> updateInventory(String id, InventoryRequest inventoryRequest);
    
    APIResponse<Boolean> deleteInventory(String id);

    void autoCleanInventoryTrash();

    PagingResponse<InventoryResponse> getWarehouseInventory(int page, int limit, String sortBy, String direction, String warehouseId, InventoryFilterRequest filter);

    PagingResponse<InventoryResponse> getStoreInventory(int page, int limit, String sortBy, String direction, String storeId, InventoryFilterRequest filter);

    APIResponse<InventoryResponse> adjustWarehouseStock(String productId, String warehouseId, int delta, String reason);

    

}
