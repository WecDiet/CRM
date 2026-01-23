package com.CRM.service.Category;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.CRM.Util.Helper.HelperService;
import com.CRM.model.Category;
import com.CRM.repository.ICategoryRepository;
import com.CRM.request.Category.CategoryRequest;
import com.CRM.response.Category.CategoryResponse;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService extends HelperService<Category, UUID> implements ICategoryService {

    @Autowired
    private ICategoryRepository iCategoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PagingResponse<CategoryResponse> getAllCategory(int page, int limit, String sortBy, String direction) {
        return getAll(
                page,
                limit,
                sortBy,
                direction,
                null,
                CategoryResponse.class,
                iCategoryRepository);
    }

    @Override
    public APIResponse<Boolean> createCategory(CategoryRequest categoryRequest) {
        if (categoryRequest.getName() == null) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }
        Category category = modelMapper.map(categoryRequest, Category.class);
        category.setInActive(categoryRequest.isActive());
        category.setCreatedDate(new Date());
        category.setModifiedDate(new Date());
        category.setCode(randomCode());
        category.setDeletedAt(0L);
        category.setDeleted(false);
        iCategoryRepository.save(category);
        return new APIResponse<>(true, "Category created successfully");
    }

    @Override
    public APIResponse<Boolean> updateCategory(String id, CategoryRequest updateCategoryRequest) {
        Category category = iCategoryRepository.findById(UUID.fromString(id)).orElse(null);
        if (category == null) {
            throw new IllegalArgumentException("Category not found with id: " + id);
        }
        modelMapper.map(updateCategoryRequest, category);
        category.setInActive(updateCategoryRequest.isActive());
        category.setModifiedDate(new Date());
        iCategoryRepository.save(category);
        return new APIResponse<>(true, "Category updated successfully");
    }

    @Override
    public APIResponse<Boolean> deleteCategory(String id) {
        Category category = iCategoryRepository.findById(UUID.fromString(id)).orElse(null);
        if (category == null) {
            throw new IllegalArgumentException("Category not found with id: " + id);
        }
        iCategoryRepository.delete(category);
        return new APIResponse<>(true, "Category deleted successfully");
    }

}
