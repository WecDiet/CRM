package com.CRM.service.InventoryTransaction;

import com.CRM.request.Inventory.InventoryTransactionFilterRequest;
import com.CRM.response.Inventory.InventoryTransactionResponse;
import com.CRM.response.Pagination.PagingResponse;

public interface IInventoryTransactionService {
     PagingResponse<InventoryTransactionResponse> getAllInventoryTransaction(int page, int limit, String sortBy, String direction, InventoryTransactionFilterRequest filter);

     PagingResponse<InventoryTransactionResponse> getWarehouseTransactions(int page, int limit, String sortBy, String direction, String warehouseId, String productId, InventoryTransactionFilterRequest filter);

     PagingResponse<InventoryTransactionResponse> getStoreTransactions(int page, int limit, String sortBy, String direction, InventoryTransactionFilterRequest filter);
}
