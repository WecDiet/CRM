package com.CRM.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.CRM.constant.Enpoint;
import com.CRM.request.Brand.brandRequest;
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
            @RequestParam int limit,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(brandService.getAllBrand(page, limit, sortBy, direction));
    }

    @PostMapping(Enpoint.Brand.CREATE)
    public ResponseEntity<?> createBrand(
            @ModelAttribute brandRequest createRequest,
            @RequestParam("media") MultipartFile media,
            @RequestParam("width") int width,
            @RequestParam("height") int height) {
        return ResponseEntity.ok(brandService.createBrand(createRequest, media, width, height));
    }

    @PutMapping(Enpoint.Brand.UPDATE)
    public ResponseEntity<?> updateBrand(
            @PathVariable Long id,
            @ModelAttribute brandRequest updateRequest,
            @RequestParam("media") MultipartFile media,
            @RequestParam("width") int width,
            @RequestParam("height") int height) {
        return ResponseEntity.ok(brandService.updateBrand(id, updateRequest, media, width, height));
    }

    @DeleteMapping(Enpoint.Brand.DELETE)
    public ResponseEntity<?> deleteBrand(@PathVariable Long id) {
        return ResponseEntity.ok(brandService.deleteBrand(id));
    }

}
