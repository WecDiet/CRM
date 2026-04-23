package com.CRM.controller;

import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.CRM.constant.Endpoint;
import com.CRM.request.Supplier.SupplierFilterRequest;
import com.CRM.request.Supplier.SupplierRequest;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;
import com.CRM.response.Supplier.SupplierDetailResponse;
import com.CRM.response.Supplier.SupplierResponse;
import com.CRM.service.Supplier.SupplierService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(Endpoint.Supplier.BASE)
public class SupplierController {
    private final SupplierService supplierService;


    @GetMapping
    public ResponseEntity<PagingResponse<SupplierResponse>> getAllSupplier(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int limit,
        @RequestParam(defaultValue = "createdDate") String sortBy,
        @RequestParam(defaultValue = "asc") String direction,
        @RequestParam(defaultValue = "true") boolean active,
        @ModelAttribute SupplierFilterRequest filter
    ){
        return ResponseEntity.ok(supplierService.getAllSupplier(page, limit, sortBy, direction, active, filter));
    }


    @GetMapping(Endpoint.Supplier.ID)
    public ResponseEntity<APIResponse<SupplierDetailResponse>> getSupplierDetail(String id){
        return ResponseEntity.ok(supplierService.getSupplierDetail(id));
    }

    @PostMapping(Endpoint.Supplier.CREATE)
    public ResponseEntity<?> createSupplier(
            @ModelAttribute SupplierRequest supplierRequest,
            @RequestParam("active") boolean active,
            @RequestParam("image") MultipartFile image
    ) throws BadRequestException{
        return ResponseEntity.ok(supplierService.createSupplier(supplierRequest, image, active));
    }

    @PutMapping(Endpoint.Supplier.UPDATE)
    public ResponseEntity<APIResponse<Boolean>> updateSupplier(
        @PathVariable String id,
        @ModelAttribute SupplierRequest supplierRequest,
        @RequestParam("image") MultipartFile image,
        @RequestParam("active") Boolean active
    ){
        return ResponseEntity.ok(supplierService.updateSupplier(id, supplierRequest, image, active));
    }


    @DeleteMapping(Endpoint.Supplier.DELETE)
    public ResponseEntity<APIResponse<Boolean>> deleteSupplier(@PathVariable String id){
        return ResponseEntity.ok(supplierService.deleteSupplier(id));
    }

    @GetMapping(Endpoint.Supplier.TRASH)
    public ResponseEntity<PagingResponse<SupplierResponse>> getAllSupplierTrash(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @ModelAttribute SupplierFilterRequest filter
        ){
            return ResponseEntity.ok(supplierService.getAllSupplierTrash(page, limit, sortBy, direction, filter));
    }
}
