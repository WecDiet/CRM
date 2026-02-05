package com.CRM.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.CRM.constant.Enpoint;
import com.CRM.request.Product.ProductFilter;
import com.CRM.request.Warehouse.WarehouseRequest;
import com.CRM.service.Warehouse.WarehouseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(Enpoint.Warehouse.BASE)
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping
    public ResponseEntity<?> getAllWarehouse(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(defaultValue = "true") boolean active,
            @ModelAttribute WarehouseRequest filter) {

        return ResponseEntity.ok(warehouseService.getAllWarehouses(page, limit, sortBy, direction, active, filter));
    }

    @GetMapping(Enpoint.Warehouse.TRASH)
    public ResponseEntity<?> getAllWarehouseTrash(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @ModelAttribute WarehouseRequest filter) {

        return ResponseEntity.ok(warehouseService.getAllWarehouseTrash(page, limit, sortBy, direction, filter));
    }

    @PostMapping(Enpoint.Warehouse.CREATE)
    public ResponseEntity<?> createWarehouse(
            @ModelAttribute WarehouseRequest warehouseRequest,
            @RequestParam("active") boolean active,
            @RequestParam("images") List<MultipartFile> images,
            @RequestParam("width") int width,
            @RequestParam("height") int height) {
        return ResponseEntity.ok(warehouseService.createWarehouse(warehouseRequest, active, images, width, height));
    }

    @DeleteMapping(Enpoint.Warehouse.DELETE)
    public ResponseEntity<?> deleteWarehouse(@RequestParam("id") String id) {
        return ResponseEntity.ok(warehouseService.deleteWarehouse(id));
    }
}