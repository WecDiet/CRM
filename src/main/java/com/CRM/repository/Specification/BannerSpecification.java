package com.CRM.repository.Specification;

import org.springframework.data.jpa.domain.Specification;

import com.CRM.model.Banner;

public class BannerSpecification extends BaseSpecification {
    public static Specification<Banner> getAllBanner() {
        return BaseSpecification.getAll();
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
