package com.CRM.repository.Specification;

import org.springframework.data.jpa.domain.Specification;

import com.CRM.model.Brand;

public class BrandSpecification extends BaseSpecification {
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

}
