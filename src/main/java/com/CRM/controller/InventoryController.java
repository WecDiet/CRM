package com.CRM.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.CRM.constant.Enpoint;
import com.CRM.request.Inventory.InventoryFilterRequest;
import com.CRM.service.Product.InventoryService;
import com.CRM.service.Product.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(Enpoint.Inventory.BASE)
public class InventoryController {
    
    private final InventoryService inventoryService;

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<?> getAllInventory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction){
        return ResponseEntity.ok(inventoryService.getAllInventories(page, limit, sortBy, direction));
    }

    // @PutMapping(Enpoint.Inventory.UPDATE)
    // public ResponseEntity<?> updateInventory(
    //         @RequestParam String id,
    //         @RequestBody InventoryRequest inventoryRequest){
    //     return ResponseEntity.ok(inventoryService.updateInventory(id, inventoryRequest));
    // }

    @DeleteMapping(Enpoint.Inventory.DELETE)
    public ResponseEntity<?> deleteInventory(@PathVariable String id){
        return ResponseEntity.ok(inventoryService.deleteInventory(id));
    }

    @GetMapping(Enpoint.Inventory.PRODUCT)
    public ResponseEntity<?> getAllProductInventory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @ModelAttribute InventoryFilterRequest filter
    ){
        return ResponseEntity.ok(productService.getAllProductInventoty(page, limit, sortBy, direction, filter));
    }
}
