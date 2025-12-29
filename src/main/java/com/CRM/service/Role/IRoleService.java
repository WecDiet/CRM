package com.CRM.service.Role;

import com.CRM.request.Product.ProductFilter;
import com.CRM.request.Role.createRoleRequest;
import com.CRM.request.Role.updateRoleRequest;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;
import com.CRM.response.Role.RoleResponse;

public interface IRoleService {
    public PagingResponse<RoleResponse> getAllRoles(
            int page, int limit, String sortBy, String direction);

    public APIResponse<RoleResponse> getRoleById(Long id);

    public APIResponse<Boolean> createRole(createRoleRequest createRoleRequest);

    public APIResponse<Boolean> updateRole(Long id, updateRoleRequest updateRoleRequest);

    public APIResponse<Boolean> deleteRole(Long id);
}
