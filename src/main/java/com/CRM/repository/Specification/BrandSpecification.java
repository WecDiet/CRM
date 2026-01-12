package com.CRM.repository.Specification;

import org.springframework.data.jpa.domain.Specification;

import com.CRM.model.Brand;
import com.CRM.model.Role;

public class BrandSpecification {
    public static Specification<Brand> getAllBrand() {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("inActive"), false),
                    criteriaBuilder.equal(root.get("isDeleted"), false),
                    criteriaBuilder.equal(root.get("deletedAt"), 0L));
        };
    }

    public static Specification<Brand> getAllBrandTrash() {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("inActive"), true),
                    criteriaBuilder.equal(root.get("isDeleted"), true),
                    criteriaBuilder.greaterThan(root.get("deletedAt"), 0L));
        };
    }

    public static Specification<Brand> deleteThreshold(long threshold) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("inActive"), true),
                    criteriaBuilder.equal(root.get("isDeleted"), true),
                    criteriaBuilder.lessThanOrEqualTo(root.get("deletedAt"), threshold));
        };
    }

    public static Specification<Brand> warningThreshold(long threshold) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("inActive"), true),
                    criteriaBuilder.equal(root.get("isDeleted"), true),
                    criteriaBuilder.greaterThan(root.get("deletedAt"), threshold));
        };
    }

}
