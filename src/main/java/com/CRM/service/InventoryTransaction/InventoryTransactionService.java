package com.CRM.service.InventoryTransaction;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.CRM.Util.Helper.HelperService;
import com.CRM.model.InventoryTransaction;
import com.CRM.repository.IInventoryTransactionRepository;
import com.CRM.repository.Specification.InventorySpecification;
import com.CRM.request.Inventory.InventoryTransactionFilterRequest;
import com.CRM.response.Inventory.InventoryTransactionResponse;
import com.CRM.response.Pagination.PagingResponse;
import com.CRM.service.Inventory.IInventoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryTransactionService extends HelperService<InventoryTransaction, UUID> implements IInventoryTransactionService {
    
    private final IInventoryTransactionRepository iIventoryTransactionRepository;
    
    @Override
    public PagingResponse<InventoryTransactionResponse> getAllInventoryTransaction(int page, int limit, String sortBy, String direction, InventoryTransactionFilterRequest filter) {
        return getAll(
                    page, 
                    limit, 
                    sortBy, 
                    direction, 
                    InventorySpecification.getAllInventoryTransaction(filter),
                    InventoryTransactionResponse.class, 
                    iIventoryTransactionRepository);
    }

    @Override
    public PagingResponse<InventoryTransactionResponse> getWarehouseTransactions(int page, int limit, String sortBy, String direction, String warehouseId, String productId, InventoryTransactionFilterRequest filter) {
        return getAll(
                    page, 
                    limit, 
                    sortBy, 
                    direction, 
                    InventorySpecification.getAllTransactionWarehouse(warehouseId, productId, filter), 
                    InventoryTransactionResponse.class, 
                    iIventoryTransactionRepository);
    }

    @Override
    public PagingResponse<InventoryTransactionResponse> getStoreTransactions(int page, int limit, String sortBy,
            String direction, InventoryTransactionFilterRequest filter) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getStoreTransactions'");
    }
    
}
