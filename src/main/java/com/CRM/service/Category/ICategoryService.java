package com.CRM.service.Category;

import java.util.UUID;

import com.CRM.request.Category.CategoryRequest;
import com.CRM.response.Category.CategoryResponse;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;

public interface ICategoryService {
    PagingResponse<CategoryResponse> getAllCategory(
            int page, int limit, String sortBy, String direction);

    APIResponse<Boolean> createCategory(CategoryRequest categoryRequest);

    APIResponse<Boolean> updateCategory(String id, CategoryRequest updateCategoryRequest);

    APIResponse<Boolean> deleteCategory(String id);
}
