package com.CRM.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.CRM.constant.Enpoint;
import com.CRM.request.Product.ProductFilter;
import com.CRM.service.Product.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(Enpoint.Product.BASE)
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<?> getAllProduct(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam int limit,
            @RequestParam(defaultValue = "price") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @ModelAttribute ProductFilter filter) {
        return ResponseEntity.ok(productService.getAllProducts(page, limit, sortBy, direction, filter));

    }

    @GetMapping(Enpoint.Product.ID)
    public ResponseEntity<?> getProductById(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
}
