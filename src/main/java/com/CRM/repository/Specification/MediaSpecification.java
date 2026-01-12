package com.CRM.repository.Specification;

import org.springframework.data.jpa.domain.Specification;

import com.CRM.model.Media;

public class MediaSpecification {
    public static Specification<Media> deleteThreshold(long threshold) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("inActive"), true),
                    criteriaBuilder.equal(root.get("isDeleted"), true),
                    criteriaBuilder.lessThanOrEqualTo(root.get("deletedAt"), threshold));
        };
    }

    public static Specification<Media> warningThreshold(long threshold) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("inActive"), true),
                    criteriaBuilder.equal(root.get("isDeleted"), true),
                    criteriaBuilder.greaterThan(root.get("deletedAt"), threshold));
        };
    }
}
