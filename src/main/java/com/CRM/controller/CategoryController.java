package com.CRM.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.CRM.constant.Enpoint;
import com.CRM.request.Category.CategoryRequest;
import com.CRM.service.Category.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping(Enpoint.Category.BASE)
    public ResponseEntity<?> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        return ResponseEntity.ok(categoryService.getAllCategory(page, limit, sortBy, direction));
    }

    @PostMapping(Enpoint.Category.BASE)
    public ResponseEntity<?> createCategory(@RequestBody CategoryRequest categoryRequest) {
        return ResponseEntity.ok(categoryService.createCategory(categoryRequest));
    }

    @PutMapping(Enpoint.Category.BASE)
    public ResponseEntity<?> updateCategory(@RequestParam String id,
            @RequestBody CategoryRequest updateCategoryRequest) {
        return ResponseEntity.ok(categoryService.updateCategory(id, updateCategoryRequest));
    }

    @DeleteMapping(Enpoint.Category.BASE)
    public ResponseEntity<?> deleteCategory(@RequestParam String id) {
        return ResponseEntity.ok(categoryService.deleteCategory(id));
    }
}
