package com.CRM.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.CRM.constant.Endpoint;
import com.CRM.request.Inventory.InventoryFilterRequest;
import com.CRM.request.Inventory.InventoryTransactionFilterRequest;
import com.CRM.response.Inventory.InventoryResponse;
import com.CRM.response.Inventory.InventoryTransactionResponse;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;
import com.CRM.service.Inventory.InventoryService;
import com.CRM.service.InventoryTransaction.InventoryTransactionService;
import com.CRM.service.Product.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(Endpoint.Inventory.BASE)
public class InventoryController {
    
    private final InventoryService inventoryService;

    private final InventoryTransactionService inventoryTransactionService;
    // @GetMapping
    // public ResponseEntity<?> getAllInventory(
    //         @RequestParam(defaultValue = "0") int page,
    //         @RequestParam(defaultValue = "5") int limit,
    //         @RequestParam(defaultValue = "createdDate") String sortBy,
    //         @RequestParam(defaultValue = "asc") String direction){
    //     return ResponseEntity.ok(inventoryService.getAllInventories(page, limit, sortBy, direction));
    // }

    // @PutMapping(Enpoint.Inventory.UPDATE)
    // public ResponseEntity<?> updateInventory(
    //         @RequestParam String id,
    //         @RequestBody InventoryRequest inventoryRequest){
    //     return ResponseEntity.ok(inventoryService.updateInventory(id, inventoryRequest));
    // }

    @DeleteMapping(Endpoint.Inventory.DELETE)
    public ResponseEntity<?> deleteInventory(@PathVariable String id){
        return ResponseEntity.ok(inventoryService.deleteInventory(id));
    }

    // @GetMapping(Enpoint.Inventory.PRODUCT)
    // public ResponseEntity<?> getAllProductInventory(
    //         @RequestParam(defaultValue = "0") int page,
    //         @RequestParam(defaultValue = "5") int limit,
    //         @RequestParam(defaultValue = "createdDate") String sortBy,
    //         @RequestParam(defaultValue = "asc") String direction,
    //         @ModelAttribute InventoryFilterRequest filter
    // ){
    //     return ResponseEntity.ok(productService.getAllProductInventoty(page, limit, sortBy, direction, filter));
    // }

    @GetMapping(Endpoint.Inventory.PRODUCT_WAREHOUSE)
    public ResponseEntity<?> getAllProductWarehouse(
        @RequestParam String id,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int limit,
        @RequestParam(defaultValue = "createdDate") String sortBy,
        @RequestParam(defaultValue = "asc") String direction,
        @ModelAttribute InventoryFilterRequest filter
    ){
        return ResponseEntity.ok(inventoryService.getWarehouseInventory(page, limit, sortBy, direction, id, filter));
    }

    @GetMapping(Endpoint.Inventory.PRODUCT_STORE)
    public ResponseEntity<?> getAllProductStore(
        @RequestParam String id,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int limit,
        @RequestParam(defaultValue = "createdDate") String sortBy,
        @RequestParam(defaultValue = "asc") String direction,
        @ModelAttribute InventoryFilterRequest filter
    ){
        return ResponseEntity.ok(inventoryService.getStoreInventory(page, limit, sortBy, direction, id, filter));
    }


    @GetMapping(Endpoint.Inventory.TRANSACTION)
    public ResponseEntity<PagingResponse<InventoryTransactionResponse>> getAllInventoryTransaction(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int limit,
        @RequestParam(defaultValue = "createdDate") String sortBy,
        @RequestParam(defaultValue = "asc") String direction,
        @ModelAttribute InventoryTransactionFilterRequest filter
    ){
        return ResponseEntity.ok(inventoryTransactionService.getAllInventoryTransaction(page, limit, sortBy, direction, filter));
    }


    @GetMapping(Endpoint.Inventory.TRANSACTION_WAREHOUSE)
    public ResponseEntity<PagingResponse<InventoryTransactionResponse>> getWarehouseTransactions(
        @RequestParam String productId,
        @RequestParam String warehouseId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int limit,
        @RequestParam(defaultValue = "createdDate") String sortBy,
        @RequestParam(defaultValue = "asc") String direction,
        @ModelAttribute InventoryTransactionFilterRequest filter
    ){
        return ResponseEntity.ok(inventoryTransactionService.getWarehouseTransactions(page, limit, sortBy, direction, warehouseId, productId, filter));
    }


    /** Điều chỉnh tồn kho thủ công (kiểm kho) */
    @PostMapping(Endpoint.Inventory.ADJUST)
    public ResponseEntity<APIResponse<InventoryResponse>> adjust(
        @PathVariable String warehouseId,
        @RequestParam String productId,
        @RequestParam int delta,
        @RequestParam String reason
    ){
        return ResponseEntity.ok(inventoryService.adjustWarehouseStock(productId, warehouseId, delta, reason));
    }
}
