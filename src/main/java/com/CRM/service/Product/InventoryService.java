package com.CRM.service.Product;

import java.util.Date;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.CRM.Util.Helper.HelperService;
import com.CRM.model.Inventory;
import com.CRM.model.Store;
import com.CRM.repository.IInventoryRepository;
import com.CRM.repository.IStoreRepository;
import com.CRM.repository.Specification.InventorySpecification;
import com.CRM.repository.Specification.ProductSpecification;
import com.CRM.request.Inventory.InventoryFilterRequest;
import com.CRM.request.Inventory.InventoryRequest;
import com.CRM.response.Inventory.InventoryResponse;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;
import com.CRM.response.Product.Inventory.InventoryProduct;
import com.CRM.service.Inventory.IInventoryService;

import jakarta.transaction.Transactional;
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
    public APIResponse<Boolean> deleteInventory(String id) {
        Inventory inventory = iInventoryRepository.findById(UUID.fromString(id)).orElseThrow(
            () -> new RuntimeException("Inventory not found")
        );
        inventory.setInActive(false);
        inventory.setDeleted(true);
        inventory.setDeletedAt(System.currentTimeMillis() / 1000);
        iInventoryRepository.save(inventory);
        return new APIResponse<>(true, "Inventory deleted successfully");
    }

    @Override
    @Transactional
    @Scheduled(fixedRate = 60 * 1000) // Quét mõi 1 phút / 1 lần
    public void autoCleanInventoryTrash() {
        long currentTime = System.currentTimeMillis() / 1000;
        long duration = 2L * 60; // 2 phút xóa
        int warningMinutes = 1; // 1 phút cảnh báo

        long warningThreshold = currentTime - (duration - (warningMinutes * 60L));
        long deleteThreshold = currentTime - duration;

        cleanTrash(
            iInventoryRepository, 
            InventorySpecification.warningThreshold(warningThreshold), 
            InventorySpecification.deleteThreshold(deleteThreshold), 
            warningMinutes, 
            "INVENTORY", 
            null);
    }

    
    // @Override
    // public APIResponse<Boolean> updateInventory(String id, InventoryRequest inventoryRequest) {
    //     Inventory inventory = iInventoryRepository.findById(UUID.fromString(id)).orElseThrow(
    //         () -> new RuntimeException("Inventory not found")
    //     );

    //     Store store = iStoreRepository.findById(UUID.fromString(inventoryRequest.getStoreId())).orElseThrow(() -> new IllegalArgumentException("Store not found"));
    //     modelMapper.map(inventoryRequest, inventory);
    //     inventory.setStore(store);
    //     inventory.setInActive(true);
    //     inventory.setModifiedDate(new Date());
    //     iInventoryRepository.save(inventory);
    //     return new APIResponse<>(true, "Inventory updated successfully");
    // }

    
}
