package com.CRM.repository.Specification;

import org.springframework.data.jpa.domain.Specification;

import com.CRM.model.Banner;

public class BannerSpecification {
    public static Specification<Banner> getAllBanner(boolean active) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("inActive"), active),
                    criteriaBuilder.equal(root.get("isDeleted"), false),
                    criteriaBuilder.equal(root.get("deletedAt"), 0L));
        };
    }

    public static Specification<Banner> getAllBannerTrash() {
        return BaseSpecification.getAllTrash();
    }

    public static Specification<Banner> deleteThreshold(long threshold) {
        return BaseSpecification.deleteThreshold(threshold);
    }

    public static Specification<Banner> warningThreshold(long threshold) {
        return BaseSpecification.warningThreshold(threshold);
    }
}
