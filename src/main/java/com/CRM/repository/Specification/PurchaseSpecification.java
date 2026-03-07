package com.CRM.repository.Specification;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import com.CRM.model.PurchaseOrder;
import com.CRM.request.PurchaseOrder.PurchaseOrderFilterRequest;

public class PurchaseSpecification {
    public static Specification<PurchaseOrder> getAllPurchaseOrderByFilter(PurchaseOrderFilterRequest filter, boolean active) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getName() != null && !filter.getName().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + filter.getName() + "%"));
            }

            if (filter.getSupplierName() != null && !filter.getSupplierName().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("supplier").get("name")), "%" + filter.getSupplierName().toLowerCase() + "%"));
            }

            if (filter.getWarehouseName() != null && !filter.getWarehouseName().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("warehouse").get("name")), "%" + filter.getWarehouseName().toLowerCase() + "%"));
            }

            if (filter.getStatus() != null && !filter.getStatus().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("status")), "%" + filter.getStatus().trim() + "%"));
            }

            if (filter.getOrderDay() != null) {
                predicates.add(criteriaBuilder.equal(root.get("orderDay"), filter.getOrderDay()));
            }

            if (filter.getOrderMonth() != null) {
                predicates.add(criteriaBuilder.equal(root.get("orderMonth"), filter.getOrderMonth()));
            }

            if (filter.getOrderYear() != null) {
                predicates.add(criteriaBuilder.equal(root.get("orderYear"), filter.getOrderYear()));
            }

            predicates.add(criteriaBuilder.equal(root.get("inActive"), active));
            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), false));
            predicates.add(criteriaBuilder.equal(root.get("deletedAt"), 0L));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<PurchaseOrder> getAllPurchaseOrderTrashFilter(PurchaseOrderFilterRequest filter){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getName() != null && !filter.getName().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + filter.getName() + "%"));
            }

            if (filter.getSupplierName() != null && !filter.getSupplierName().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("supplier").get("name")), "%" + filter.getSupplierName().toLowerCase() + "%"));
            }

            if (filter.getWarehouseName() != null && !filter.getWarehouseName().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("warehouse").get("name")), "%" + filter.getWarehouseName().toLowerCase() + "%"));
            }

            if (filter.getStatus() != null && !filter.getStatus().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("status")), "%" + filter.getStatus().trim() + "%"));
            }

            if (filter.getOrderDay() != null) {
                predicates.add(criteriaBuilder.equal(root.get("orderDay"), filter.getOrderDay()));
            }

            if (filter.getOrderMonth() != null) {
                predicates.add(criteriaBuilder.equal(root.get("orderMonth"), filter.getOrderMonth()));
            }

            if (filter.getOrderYear() != null) {
                predicates.add(criteriaBuilder.equal(root.get("orderYear"), filter.getOrderYear()));
            }


            predicates.add(criteriaBuilder.equal(root.get("inActive"), false));
            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), true));
            predicates.add(criteriaBuilder.greaterThan(root.get("deletedAt"), 0L));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<PurchaseOrder> deleteThreshold(long threshold) {
        return BaseSpecification.deleteThreshold(threshold);
    }

    public static Specification<PurchaseOrder> warningThreshold(long threshold) {
        return BaseSpecification.warningThreshold(threshold);
    }
}
