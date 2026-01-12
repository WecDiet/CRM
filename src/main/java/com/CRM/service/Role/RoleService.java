package com.CRM.service.Role;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.CRM.Util.Helper.HelperService;
import com.CRM.enums.RestoreEnum;
import com.CRM.model.Role;
import com.CRM.repository.IRoleRepository;
import com.CRM.repository.Specification.RoleSpecification;
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
        if (iRoleRepository.existsActiveByName(createRoleRequest.getName())) {
            throw new IllegalArgumentException("Role name already exists and is active");
        }
        Role role = modelMapper.map(createRoleRequest, Role.class);
        role.setInActive(false);
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
        role.setInActive(true);
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

    /*
     * - Nó sẽ tự động chạy ngầm để quét những dữ liệu nào đạt tới thời gian hạn
     * mức. Và đặc biệt là nó thông báo trước khi xóa cứng
     */
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
                "ROLE",
                null);
    }

    @Override
    public APIResponse<Boolean> restoreRole(String id, RestoreEnum action) {
        // Tìm bản ghi trong thùng rác
        Role roleInTrash = iRoleRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new IllegalArgumentException("This role doesn't belong in the trash can."));

        // Tìm bản ghi trùng đang hoạt động
        Optional<Role> activeDuplicate = iRoleRepository.findActiveByName(roleInTrash.getName());

        if (activeDuplicate.isPresent()) {
            // Nếu user chưa xác nhận hành động (lần gọi đầu tiên)
            if (action == null || action == RestoreEnum.RESTORE) {
                throw new IllegalArgumentException("CONFLICT: A role with this name already exists.");
            }

            // Nếu user chọn Bỏ qua
            if (action == RestoreEnum.CANCEL) {
                return new APIResponse<>(false, List.of("Restore operation was cancelled."));
            }

            // Nếu user chọn Ghi đè (OVERWRITE)
            if (action == RestoreEnum.OVERWRITE) {
                // Xóa role đang active trước
                iRoleRepository.delete(activeDuplicate.get());
                iRoleRepository.flush(); // Xóa ngay để tránh trùng Unique Key khi save bên dưới
            }
        }
        roleInTrash.setInActive(false);
        roleInTrash.setDeleted(false);
        roleInTrash.setDeletedAt(0L);

        // roleInTrash.setCode(null);

        iRoleRepository.save(roleInTrash);
        return new APIResponse<>(true, List.of("Restored successfully."));
    }
}
