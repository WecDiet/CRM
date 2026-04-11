package com.CRM.service.Inventory;
import java.util.UUID;

import org.apache.coyote.BadRequestException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.CRM.Util.Helper.HelperService;
import com.CRM.model.Inventory;
import com.CRM.model.InventoryTransaction;
import com.CRM.model.Product;
import com.CRM.model.Store;
import com.CRM.model.Warehouse;
import com.CRM.repository.IInventoryRepository;
import com.CRM.repository.IInventoryTransactionRepository;
import com.CRM.repository.IProductRepository;
import com.CRM.repository.IStoreRepository;
import com.CRM.repository.IWarehouseRepository;
import com.CRM.repository.Specification.InventorySpecification;
import com.CRM.request.Inventory.InventoryFilterRequest;
import com.CRM.response.Inventory.InventoryResponse;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryService extends HelperService<Inventory, UUID> implements IInventoryService {

    private final IInventoryRepository iInventoryRepository;

    private final IStoreRepository iStoreRepository;

    private final IWarehouseRepository iWarehouseRepository;

    private final IProductRepository iProductRepository;

    private final IInventoryTransactionRepository iInventoryTransactionRepository;

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
    @Transactional
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


    // Toàn bộ tồn kho tại kho tổng
    @Override
    public PagingResponse<InventoryResponse> getWarehouseInventory(int page, int limit, String sortBy,
            String direction, String warehouseId, InventoryFilterRequest filter) {
        Warehouse warehouse = iWarehouseRepository.findById(UUID.fromString(warehouseId)).orElse(null);
        if (warehouse == null) {
            throw new IllegalArgumentException("Warehouse not found");
        }
        return getAll(
                    page, 
                    limit, 
                    sortBy, 
                    direction, 
                    InventorySpecification.getAllProductWarehouse(warehouseId, filter), 
                    InventoryResponse.class,
                    iInventoryRepository);
    }


    // Toàn bộ tồn kho tại cửa hàng
    @Override
    public PagingResponse<InventoryResponse> getStoreInventory(int page, int limit, String sortBy, String direction,
            String storeId, InventoryFilterRequest filter) {
        Store store = iStoreRepository.findById(UUID.fromString(storeId)).orElse(null);
        if (store == null) {
            throw new IllegalArgumentException("Store not found.");
        }

        return getAll(
                    page, 
                    limit, 
                    sortBy, 
                    direction, 
                    InventorySpecification.getAllProductByStore(storeId, filter),
                    InventoryResponse.class,
                    iInventoryRepository
                );
    }

    @Override
    @Transactional
    public APIResponse<InventoryResponse> adjustWarehouseStock(String productId, String warehouseId, int delta, String reason) {
        
        Warehouse warehouse = iWarehouseRepository.findById(UUID.fromString(warehouseId)).orElseThrow(() -> new IllegalArgumentException("Warehouse not found."));

        Product product = iProductRepository.findById(UUID.fromString(productId)).orElseThrow(() -> new IllegalArgumentException("Product not found"));

        try {
            Inventory inventory = iInventoryRepository.findByProductAndWarehouseWithLock(product, warehouse).orElseGet(() -> {
                Inventory newInventory = Inventory.builder()
                        .product(product)
                        .warehouse(warehouse)
                        .store(null)
                        .quantityOnHand(0)
                        .build(); 
                return iInventoryRepository.save(newInventory);
            });

            int newQuantity = inventory.getQuantityOnHand() + delta;
            if (newQuantity < 0) {
                throw new BadRequestException(String.format("Cannot be adjusted: current inventory %d, delta %d → negative %d", inventory.getQuantityOnHand(), delta, newQuantity));
            }

            inventory.setQuantityOnHand(newQuantity);
            iInventoryRepository.save(inventory);

            String type = delta >= 0 ? "ADJUST_IN" : "ADJUST_OUT";

            iInventoryTransactionRepository.save(
                    InventoryTransaction.builder()
                    .product(product)
                    .warehouse(warehouse)
                    .store(null)
                    .type(type)
                    .quantity(delta)
                    .referenceId(warehouse.getPurchaseOrders().get(0).getId())
                    .referenceCode(warehouse.getPurchaseOrders().get(0).getPoNumber())
                    .note("MANUAL: " + reason)
                    .build()
                );
            
                InventoryResponse inventoryResponse = modelMapper.map(inventory, InventoryResponse.class);
                return new APIResponse<>(inventoryResponse, "Adjust warehouse stock successfully");
            // return new APIResponse<>(toInventoryResponse(inventory),"Adjust warehouse stock successfully");
        } catch (Exception e) {
            iInventoryRepository.findByProductAndWarehouseWithLock(product, warehouse).orElseThrow(() -> new RuntimeException("Inventory should exist but not found"));
            return new APIResponse<>(null,  "Failed to adjust warehouse stock: " + e.getMessage());
        }
    }


    private InventoryResponse toInventoryResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .id(inventory.getId())
                .productId(inventory.getProduct().getId())
                .productName(inventory.getProduct().getProductDetail().getName())
                .mainImage(inventory.getProduct().getMainImage().toString())
                .locationName(inventory.getWarehouse().getName())
                .locationType(inventory.getWarehouse().getWarehouseType())
                .quantityOnHand(inventory.getQuantityOnHand())
                .build();
    }
}
