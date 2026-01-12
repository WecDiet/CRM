package com.CRM.service.Role;

import java.util.List;
import java.util.UUID;

import com.CRM.enums.RestoreEnum;
import com.CRM.request.Role.createRoleRequest;
import com.CRM.request.Role.updateRoleRequest;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;
import com.CRM.response.Role.RoleResponse;

public interface IRoleService {
    PagingResponse<RoleResponse> getAllRoles(
            int page, int limit, String sortBy, String direction);

    APIResponse<RoleResponse> getRoleById(UUID id);

    APIResponse<Boolean> createRole(createRoleRequest createRoleRequest);

    APIResponse<Boolean> updateRole(UUID id, updateRoleRequest updateRoleRequest);

    APIResponse<Boolean> deleteRole(UUID id);

    PagingResponse<RoleResponse> getAllRoleTrash(int page, int limit, String sortBy, String direction);

    // public APIResponse<Boolean> deleteMultiRole(List<String> roleList);
    void autoCleanRoleTrash();

    APIResponse<Boolean> restoreRole(String id, RestoreEnum action);

}
