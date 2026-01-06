package com.CRM.service.Role;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.CRM.Util.Helper.HelperService;
import com.CRM.model.Role;
import com.CRM.repository.IRoleRepository;
import com.CRM.repository.Specification.Role.RoleSpecification;
import com.CRM.request.Role.createRoleRequest;
import com.CRM.request.Role.updateRoleRequest;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;
import com.CRM.response.Role.RoleResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService extends HelperService<Role, UUID> implements IRoleService {

    @Autowired
    private IRoleRepository iRoleRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PagingResponse<RoleResponse> getAllRoles(int page, int limit, String sortBy, String direction) {
        return getAll(
                page,
                limit,
                sortBy,
                direction,
                RoleSpecification.getAllRoles(),
                RoleResponse.class,
                iRoleRepository);
    }

    @Override
    public APIResponse<RoleResponse> getRoleById(UUID id) {
        return getById(
                id,
                iRoleRepository,
                Role.class,
                RoleResponse.class);
    }

    @Override
    public APIResponse<Boolean> createRole(createRoleRequest createRoleRequest) {
        if (createRoleRequest.getName().isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be empty");
        }
        if (!createRoleRequest.getName().isEmpty()) {
            if (iRoleRepository.existsByName(createRoleRequest.getName())) {
                throw new IllegalArgumentException("Role name already exists");
            }
        }
        Role role = modelMapper.map(createRoleRequest, Role.class);
        role.setInActive(true);
        role.setCreatedDate(new Date());
        role.setModifiedDate(new Date());
        role.setCode(randomCode());
        role.setDeletedAt(0L);
        role.setDeleted(false);
        iRoleRepository.save(role);
        List<String> message = List.of("Role created successfully");
        return new APIResponse<>(true, message);
    }

    @Override
    public APIResponse<Boolean> updateRole(UUID id, updateRoleRequest updateRoleRequest) {
        Role role = iRoleRepository.findById(id).orElse(null);
        if (role == null) {
            throw new IllegalArgumentException("Role not found");
        }
        modelMapper.map(updateRoleRequest, role);
        role.setInActive(updateRoleRequest.isInActive());
        role.setModifiedDate(new Date());
        iRoleRepository.save(role);
        List<String> message = List.of("Role updated successfully");
        return new APIResponse<>(true, message);
    }

    @Override
    @Transactional
    public APIResponse<Boolean> deleteRole(UUID id) {
        Role role = iRoleRepository.findById(id).orElse(null);
        if (role == null) {
            throw new IllegalArgumentException("Role not found");
        }
        role.setInActive(false);
        role.setDeleted(true);
        role.setDeletedAt(System.currentTimeMillis() / 1000);
        iRoleRepository.save(role);
        return new APIResponse<>(true, List.of("Role deleted successfully"));
    }

    @Override
    public PagingResponse<RoleResponse> getAllRoleTrash(int page, int limit, String sortBy, String direction) {
        return getAll(
                page,
                limit,
                sortBy,
                direction,
                RoleSpecification.getAllRoleTrash(),
                RoleResponse.class,
                iRoleRepository);
    }

    @Override
    @Transactional
    @Scheduled(fixedRate = 60 * 1000) // Quét mõi 1 phút / 1 lần
    public void autoCleanRoleTrash() {
        long currentTime = System.currentTimeMillis() / 1000;
        long duration = 2L * 60; // 2 phút xóa
        int warningMinutes = 1; // 1 phút cảnh báo

        // Tính toán mốc thời gian ở đây
        long warningThreshold = currentTime - (duration - (warningMinutes * 60L));
        long deleteThreshold = currentTime - duration;
        cleanTrash(iRoleRepository,
                RoleSpecification.warningThreshold(warningThreshold), // Truyền thời gian thông báo trước khi xóa
                RoleSpecification.deleteThreshold(deleteThreshold), // Truyền thời gian sẽ bị xóa cứng
                warningMinutes,
                "ROLE");
    }
}
