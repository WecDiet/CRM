package com.CRM.service.Inventory;

import com.CRM.request.Inventory.InventoryRequest;
import com.CRM.response.Inventory.InventoryResponse;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;

public interface IInventoryService {

    PagingResponse<InventoryResponse> getAllInventories(int page, int limit, String sortBy, String direction);

    APIResponse<Boolean> updateInventory(String id, InventoryRequest inventoryRequest);

}
