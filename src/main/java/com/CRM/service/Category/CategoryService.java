package com.CRM.service.Category;

import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.CRM.model.Category;
import com.CRM.repository.ICategoryRepository;
import com.CRM.request.Category.categoryRequest;
import com.CRM.response.Category.CategoryResponse;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;
import com.CRM.service.Helper.HelperService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService extends HelperService<Category, Long> implements ICategoryService {

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
    public APIResponse<Boolean> createCategory(categoryRequest categoryRequest) {
        if (categoryRequest.getName() == null) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }
        Category category = modelMapper.map(categoryRequest, Category.class);
        category.setCreatedDate(new Date());
        category.setInActive(true);
        category.setModifiedDate(new Date());
        iCategoryRepository.save(category);
        List<String> message = List.of("Category created successfully");
        return new APIResponse<>(true, message);
    }

    @Override
    public APIResponse<Boolean> updateCategory(Long id, categoryRequest updateCategoryRequest) {
        Category category = iCategoryRepository.findById(id).orElse(null);
        if (category == null) {
            throw new IllegalArgumentException("Category not found with id: " + id);
        }
        modelMapper.map(updateCategoryRequest, category);
        category.setInActive(true);
        category.setModifiedDate(new Date());
        iCategoryRepository.save(category);
        List<String> message = List.of("Category updated successfully");
        return new APIResponse<>(true, message);
    }

    @Override
    public APIResponse<Boolean> deleteCategory(Long id) {
        Category category = iCategoryRepository.findById(id).orElse(null);
        if (category == null) {
            throw new IllegalArgumentException("Category not found with id: " + id);
        }
        iCategoryRepository.delete(category);
        List<String> message = List.of("Category deleted successfully");
        return new APIResponse<>(true, message);
    }

}
