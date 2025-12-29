package com.CRM.service.Role;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.CRM.model.Role;
import com.CRM.repository.IRoleRepository;
import com.CRM.request.Product.ProductFilter;
import com.CRM.request.Role.createRoleRequest;
import com.CRM.request.Role.updateRoleRequest;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;
import com.CRM.response.Role.RoleResponse;
import com.CRM.service.Helper.HelperService;

import ch.qos.logback.core.model.Model;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService extends HelperService<Role, Long> implements IRoleService {

    @Autowired
    private IRoleRepository iRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PagingResponse<RoleResponse> getAllRoles(int page, int limit, String sortBy, String direction) {
        return getAll(
                page,
                limit,
                sortBy,
                direction,
                null,
                RoleResponse.class,
                iRepository);
    }

    @Override
    public APIResponse<RoleResponse> getRoleById(Long id) {
        return getById(
                id,
                iRepository,
                Role.class,
                RoleResponse.class);
    }

    @Override
    public APIResponse<Boolean> createRole(createRoleRequest createRoleRequest) {
        if (createRoleRequest.getName().isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be empty");
        }
        if (!createRoleRequest.getName().isEmpty()) {
            if (iRepository.existsByName(createRoleRequest.getName())) {
                throw new IllegalArgumentException("Role name already exists");
            }
        }
        Role role = modelMapper.map(createRoleRequest, Role.class);
        role.setInActive(true);
        role.setCreatedDate(new Date());
        iRepository.save(role);
        List<String> message = new ArrayList<>();
        message.add("Create role successfully");
        return new APIResponse<>(true, message);
    }

    @Override
    public APIResponse<Boolean> updateRole(Long id, updateRoleRequest updateRoleRequest) {
        Role role = iRepository.findById(id).orElse(null);
        if (role == null) {
            throw new IllegalArgumentException("Role not found");
        }
        modelMapper.map(updateRoleRequest, role);
        role.setModifiedDate(new Date());
        iRepository.save(role);
        List<String> message = new ArrayList<>();
        message.add("Update role successfully");
        return new APIResponse<>(true, message);
    }

    @Override
    public APIResponse<Boolean> deleteRole(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteRole'");
    }

}
