package com.CRM.repository.Specification;

import org.springframework.data.jpa.domain.Specification;

import com.CRM.model.Role;

public class RoleSpecification {
    public static Specification<Role> getAllRoles() {
        return BaseSpecification.getAll();
    }

    public static Specification<Role> getAllRoleTrash() {
        return BaseSpecification.getAllTrash();
    }

    public static Specification<Role> deleteThreshold(long threshold) {
        return BaseSpecification.deleteThreshold(threshold);
    }

    public static Specification<Role> warningThreshold(long threshold) {
        return BaseSpecification.warningThreshold(threshold);
    }
}
