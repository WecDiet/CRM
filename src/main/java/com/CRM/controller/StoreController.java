package com.CRM.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.CRM.constant.Enpoint;
import com.CRM.request.Store.StoreFilterRequest;
import com.CRM.request.Store.StoreRequest;
import com.CRM.request.Warehouse.WarehouseRequest;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;
import com.CRM.response.Store.BaseStoreResponse;
import com.CRM.service.Store.StoreService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(Enpoint.Store.BASE)
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;


    @GetMapping
    public ResponseEntity<PagingResponse<BaseStoreResponse>> getAllStores(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int limit,
        @RequestParam(defaultValue = "createdDate") String sortBy,
        @RequestParam(defaultValue = "asc") String direction,
        @RequestParam(defaultValue = "true") boolean active,
        @ModelAttribute StoreFilterRequest filter
    ){
        return ResponseEntity.ok(storeService.getAllStore(page, limit, sortBy, direction, active, filter));
    }

    @PostMapping(Enpoint.Store.CREATE)
    public ResponseEntity<APIResponse<Boolean>> createStore(
        @ModelAttribute StoreRequest storeRequest,
         @RequestParam("images") List<MultipartFile> images        
    ){
        return ResponseEntity.ok(storeService.createStore(storeRequest, images));
    }
}
