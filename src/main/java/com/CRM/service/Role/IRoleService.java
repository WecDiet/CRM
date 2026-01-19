package com.CRM.service.Role;

import java.util.List;
import java.util.UUID;

import com.CRM.enums.RestoreEnum;
import com.CRM.request.Role.roleRequest;
import com.CRM.request.Role.roleRequest;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;
import com.CRM.response.Role.RoleResponse;

public interface IRoleService {
    PagingResponse<RoleResponse> getAllRoles(
            int page, int limit, String sortBy, String direction);

    APIResponse<RoleResponse> getRoleById(UUID id);

    APIResponse<Boolean> createRole(roleRequest createRoleRequest);

    APIResponse<Boolean> updateRole(String id, roleRequest updateRoleRequest);

    APIResponse<Boolean> deleteRole(String id);

    PagingResponse<RoleResponse> getAllRoleTrash(int page, int limit, String sortBy, String direction);

    // public APIResponse<Boolean> deleteMultiRole(List<String> roleList);
    void autoCleanRoleTrash();

    APIResponse<Boolean> restoreRole(String id, RestoreEnum action);

}
