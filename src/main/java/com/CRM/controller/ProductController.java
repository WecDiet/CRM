package com.CRM.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.CRM.constant.Endpoint;
import com.CRM.request.Product.ProductFilter;
import com.CRM.service.Product.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(Endpoint.Product.BASE)
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<?> getAllProduct(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int limit,
        @RequestParam(defaultValue = "createdDate") String sortBy,
        @RequestParam(defaultValue = "asc") String direction,
        @RequestParam(defaultValue = "true") boolean active,
        @ModelAttribute ProductFilter filter) {
        return ResponseEntity.ok(productService.getAllProducts(page, limit, sortBy, direction, active, filter));

    }

    @GetMapping(Endpoint.Product.ID)
    public ResponseEntity<?> getProductById(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProductDetail(id));
    }


    @DeleteMapping(Endpoint.Product.DELETE)
    public ResponseEntity<?> deleteProduct(@PathVariable String id) {
        return ResponseEntity.ok(productService.deleteProduct(id));
    }


    @GetMapping(Endpoint.Product.TRASH)
    public ResponseEntity<?> getAllProductTrash(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int limit,
        @RequestParam(defaultValue = "createdDate") String sortBy,
        @RequestParam(defaultValue = "asc") String direction,
        @ModelAttribute ProductFilter filter) {
        return ResponseEntity.ok(productService.getAllProductTrash(page, limit, sortBy, direction, filter));

    }

    @GetMapping(Endpoint.Product.TRASH_ID)
    public ResponseEntity<?> getProductTrashDetail(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProductTrashDetail(id));
    }
    
}
