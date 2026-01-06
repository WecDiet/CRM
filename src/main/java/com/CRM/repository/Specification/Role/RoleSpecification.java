package com.CRM.repository.Specification.Role;

import org.springframework.data.jpa.domain.Specification;

import com.CRM.model.Role;

public class RoleSpecification {
    public static Specification<Role> getAllRoles() {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("inActive"), true),
                    criteriaBuilder.equal(root.get("isDeleted"), false),
                    criteriaBuilder.equal(root.get("deletedAt"), 0L));
        };
    }

    public static Specification<Role> getAllRoleTrash() {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("inActive"), false),
                    criteriaBuilder.equal(root.get("isDeleted"), true),
                    criteriaBuilder.greaterThan(root.get("deletedAt"), 0L));
        };
    }

    public static Specification<Role> deleteThreshold(long threshold) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("inActive"), false),
                    criteriaBuilder.equal(root.get("isDeleted"), true),
                    criteriaBuilder.lessThanOrEqualTo(root.get("deletedAt"), threshold));
        };
    }

    public static Specification<Role> warningThreshold(long threshold) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("inActive"), false),
                    criteriaBuilder.equal(root.get("isDeleted"), true),
                    criteriaBuilder.greaterThan(root.get("deletedAt"), threshold));
        };
    }
}
