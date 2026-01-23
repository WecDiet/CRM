package com.CRM.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.CRM.constant.Enpoint;
import com.CRM.enums.RestoreEnum;
import com.CRM.request.Brand.BrandRequest;
import com.CRM.service.Brand.BrandService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(Enpoint.Brand.BASE)
public class BrandController {
    @Autowired
    private BrandService brandService;

    @GetMapping
    public ResponseEntity<?> getAllBrands(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(defaultValue = "collection") String categoryName,
            @RequestParam(defaultValue = "true") boolean active) {
        return ResponseEntity.ok(brandService.getAllBrand(page, limit, sortBy,
                direction, categoryName, active));
    }

    @PostMapping(Enpoint.Brand.CREATE)
    public ResponseEntity<?> createBrand(
            @ModelAttribute BrandRequest createRequest,
            @RequestParam("media") MultipartFile media,
            @RequestParam("width") int width,
            @RequestParam("height") int height) {
        return ResponseEntity.ok(brandService.createBrand(createRequest, media,
                width, height));
    }

    @PutMapping(Enpoint.Brand.UPDATE)
    public ResponseEntity<?> updateBrand(
            @PathVariable String id,
            @ModelAttribute BrandRequest updateRequest,
            @RequestParam("media") MultipartFile media,
            @RequestParam("width") int width,
            @RequestParam("height") int height) {
        return ResponseEntity.ok(brandService.updateBrand(id, updateRequest, media,
                width, height));
    }

    @DeleteMapping(Enpoint.Brand.DELETE)
    public ResponseEntity<?> deleteBrand(@PathVariable String id) {
        return ResponseEntity.ok(brandService.deleteBrand(id));
    }

    @GetMapping(Enpoint.Brand.TRASH)
    public ResponseEntity<?> getAllBrandTrash(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        return ResponseEntity.ok(brandService.getAllBrandTrash(page, limit, sortBy, direction));
    }

    @PatchMapping(Enpoint.Brand.RESTORE)
    public ResponseEntity<?> restoreBrand(
            @PathVariable String id,
            @RequestParam(name = "action", required = false) RestoreEnum action) {
        return ResponseEntity.ok(brandService.restoreBrand(id, action));
    }

}
