package com.CRM.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.CRM.constant.Endpoint;
import com.CRM.enums.RestoreEnum;
import com.CRM.request.Warehouse.WarehouseRequest;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.service.PurchaseOrder.PurchaseOrderService;
import com.CRM.service.Warehouse.WarehouseService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(Endpoint.Warehouse.BASE)
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

    @GetMapping(Endpoint.Warehouse.TRASH)
    public ResponseEntity<?> getAllWarehouseTrash(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @ModelAttribute WarehouseRequest filter) {

        return ResponseEntity.ok(warehouseService.getAllWarehouseTrash(page, limit, sortBy, direction, filter));
    }

    @PostMapping(Endpoint.Warehouse.CREATE)
    public ResponseEntity<?> createWarehouse(
            @ModelAttribute WarehouseRequest warehouseRequest,
            @RequestParam("active") boolean active,
            @RequestParam("images") List<MultipartFile> images) {
        return ResponseEntity.ok(warehouseService.createWarehouse(warehouseRequest, active, images));
    }

    @DeleteMapping(Endpoint.Warehouse.DELETE)
    public ResponseEntity<?> deleteWarehouse(@RequestParam("id") String id) {
        return ResponseEntity.ok(warehouseService.deleteWarehouse(id));
    }

    @PatchMapping(Endpoint.Warehouse.RESTORE)
    public ResponseEntity<?> restoreWarehouse(
            @PathVariable String id,
            @RequestParam(name = "action", required = false) RestoreEnum action) {
        return ResponseEntity.ok(warehouseService.restoreWarehouse(id, action));
    }

    @PutMapping(Endpoint.Warehouse.UPDATE)
    public ResponseEntity<?> updateWarehouse(
            @PathVariable String id,
            @ModelAttribute WarehouseRequest warehouseRequest,
            @RequestParam("active") boolean active,
            @RequestParam("images") List<MultipartFile> images,
            @RequestParam("idsImageDelete") List<String> idsImageDelete) {
        return ResponseEntity.ok(warehouseService.updateWarehouse(id, active, warehouseRequest, images, idsImageDelete));
    }

}