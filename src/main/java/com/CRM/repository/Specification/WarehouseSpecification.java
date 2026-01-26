package com.CRM.repository.Specification;

import org.springframework.data.jpa.domain.Specification;

import com.CRM.model.Warehouse;

public class WarehouseSpecification {

    public static Specification<Warehouse> getAllWarehouse() {
        return BaseSpecification.getAll();
    }

    public static Specification<Warehouse> getAllWarehouseTrash() {
        return BaseSpecification.getAllTrash();
    }

    public static Specification<Warehouse> deleteThreshold(long threshold) {
        return BaseSpecification.deleteThreshold(threshold);
    }

    public static Specification<Warehouse> warningThreshold(long threshold) {
        return BaseSpecification.warningThreshold(threshold);
    }
}
