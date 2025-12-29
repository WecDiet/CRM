package com.CRM.service.Category;

import com.CRM.request.Category.categoryRequest;
import com.CRM.response.Category.CategoryResponse;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;

public interface ICategoryService {
    public PagingResponse<CategoryResponse> getAllCategory(
            int page, int limit, String sortBy, String direction);

    public APIResponse<Boolean> createCategory(categoryRequest categoryRequest);

    public APIResponse<Boolean> updateCategory(Long id, categoryRequest updateCategoryRequest);

    public APIResponse<Boolean> deleteCategory(Long id);
}
