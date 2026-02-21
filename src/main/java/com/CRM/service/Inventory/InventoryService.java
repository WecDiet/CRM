package com.CRM.service.Inventory;

import java.util.Date;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.CRM.Util.Helper.HelperService;
import com.CRM.model.Inventory;
import com.CRM.model.Store;
import com.CRM.repository.IInventoryRepository;
import com.CRM.repository.IStoreRepository;
import com.CRM.repository.Specification.InventorySpecification;
import com.CRM.request.Inventory.InventoryRequest;
import com.CRM.response.Inventory.InventoryResponse;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryService extends HelperService<Inventory, UUID> implements IInventoryService {

    private final IInventoryRepository iInventoryRepository;

    private final IStoreRepository iStoreRepository;

    private final ModelMapper modelMapper;

    @Override
    public PagingResponse<InventoryResponse> getAllInventories(int page, int limit, String sortBy, String direction) {
       return getAll(
                    page, 
                    limit, 
                    sortBy, 
                    direction, 
                    InventorySpecification.getAllInventory(), 
                    InventoryResponse.class, 
                    iInventoryRepository);
    }

    @Override
    public APIResponse<Boolean> updateInventory(String id, InventoryRequest inventoryRequest) {
        Inventory inventory = iInventoryRepository.findById(UUID.fromString(id)).orElseThrow(
            () -> new RuntimeException("Inventory not found")
        );

        Store store = iStoreRepository.findById(UUID.fromString(inventoryRequest.getStoreId())).orElseThrow(() -> new IllegalArgumentException("Store not found"));
        modelMapper.map(inventoryRequest, inventory);
        inventory.setStore(store);
        inventory.setInActive(true);
        inventory.setDeleted(false);
        inventory.setModifiedDate(new Date());
        iInventoryRepository.save(inventory);
        return new APIResponse<>(true, "Inventory updated successfully");
    }

    

}
