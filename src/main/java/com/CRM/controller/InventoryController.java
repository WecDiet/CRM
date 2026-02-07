package com.CRM.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.CRM.constant.Enpoint;
import com.CRM.request.Inventory.InventoryRequest;
import com.CRM.service.Inventory.InventoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(Enpoint.Inventory.BASE)
public class InventoryController {
    
    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<?> getAllInventory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction){
        return ResponseEntity.ok(inventoryService.getAllInventories(page, limit, sortBy, direction));
    }

    @PutMapping(Enpoint.Inventory.UPDATE)
    public ResponseEntity<?> updateInventory(
            @RequestParam String id,
            @RequestBody InventoryRequest inventoryRequest){
        return ResponseEntity.ok(inventoryService.updateInventory(id, inventoryRequest));
    }
}
