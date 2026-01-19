package com.CRM.repository.Specification;

import org.springframework.data.jpa.domain.Specification;

import com.CRM.model.Brand;
import com.CRM.model.Category;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class BrandSpecification {
    public static Specification<Brand> getAllBrand() {
        return BaseSpecification.getAll();
    }

    public static Specification<Brand> getAllBrandTrash() {
        return BaseSpecification.getAllTrash();
    }

    public static Specification<Brand> deleteThreshold(long threshold) {
        return BaseSpecification.deleteThreshold(threshold);
    }

    public static Specification<Brand> warningThreshold(long threshold) {
        return BaseSpecification.warningThreshold(threshold);
    }

    public static Specification<Brand> getAllBrandByCategory(String categoryName, boolean active) {
        return (root, query, criteriaBuilder) -> {
            Predicate baseBrand = criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("inActive"), active),
                    criteriaBuilder.equal(root.get("isDeleted"), false),
                    criteriaBuilder.equal(root.get("deletedAt"), 0L));
            String normalizedCategory = (categoryName != null) ? categoryName.trim().toLowerCase() : "";
            if ("collection".equalsIgnoreCase(categoryName)) {
                return criteriaBuilder.and(
                        baseBrand,
                        criteriaBuilder.equal(root.get("highlighted"), true));
            }
            Join<Brand, Category> categoryJoin = root.join("category", JoinType.INNER);

            Predicate isGlobal = criteriaBuilder.equal(root.get("highlighted"), true);

            Predicate categoryBrand = criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("highlighted"), false),
                    criteriaBuilder.equal(criteriaBuilder.lower(categoryJoin.get("name")), normalizedCategory));
            return criteriaBuilder.and(
                    baseBrand,
                    criteriaBuilder.or(isGlobal, categoryBrand));
        };
    }

}
