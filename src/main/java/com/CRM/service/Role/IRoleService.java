package com.CRM.service.Role;

import java.util.List;
import java.util.UUID;

import com.CRM.request.Role.createRoleRequest;
import com.CRM.request.Role.updateRoleRequest;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;
import com.CRM.response.Role.RoleResponse;

public interface IRoleService {
    public PagingResponse<RoleResponse> getAllRoles(
            int page, int limit, String sortBy, String direction);

    public APIResponse<RoleResponse> getRoleById(UUID id);

    public APIResponse<Boolean> createRole(createRoleRequest createRoleRequest);

    public APIResponse<Boolean> updateRole(UUID id, updateRoleRequest updateRoleRequest);

    public APIResponse<Boolean> deleteRole(UUID id);

    public PagingResponse<RoleResponse> getAllRoleTrash(int page, int limit, String sortBy, String direction);

    // public APIResponse<Boolean> deleteMultiRole(List<String> roleList);
    public void autoCleanRoleTrash();
}
