package com.CRM.repository.Specification;



import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;

public class BaseSpecification {
    public static <T> Specification<T> getAll() {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("inActive"), true),
                    criteriaBuilder.equal(root.get("isDeleted"), false),
                    criteriaBuilder.equal(root.get("deletedAt"), 0L));
        };
    }

    public static <T> Specification<T> getAllTrash() {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("inActive"), false),
                    criteriaBuilder.equal(root.get("isDeleted"), true),
                    criteriaBuilder.greaterThan(root.get("deletedAt"), 0L));
        };
    }

    public static <T> Specification<T> deleteThreshold(long threshold) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("inActive"), false),
                    criteriaBuilder.equal(root.get("isDeleted"), true),
                    criteriaBuilder.lessThanOrEqualTo(root.get("deletedAt"), threshold));
        };
    }

    public static <T> Specification<T> warningThreshold(long threshold) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("inActive"), false),
                    criteriaBuilder.equal(root.get("isDeleted"), true),
                    criteriaBuilder.greaterThan(root.get("deletedAt"), threshold));
        };
    }


    public static <T, K> Specification<T> selectIdOnly(Specification<T> base) {
        return (root, query, criteriaBuilder) -> {
            ((CriteriaQuery<Object>) query).select(root.get("id"));
            return base != null
                    ? base.toPredicate(root, query, criteriaBuilder)
                    : criteriaBuilder.conjunction();
        };
    }
}
