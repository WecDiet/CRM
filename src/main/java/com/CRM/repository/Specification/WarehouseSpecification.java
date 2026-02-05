package com.CRM.repository.Specification;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import com.CRM.model.Warehouse;
import com.CRM.request.Warehouse.WarehouseRequest;


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

    public static Specification<Warehouse> getAllWarehouseFilter(WarehouseRequest filter, boolean active) {
        return (root, query, criteriaBuilder) -> {
            if (filter == null) {
                System.out.println("Active at Specification NULL: " + active);
                return criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("inActive"), true),
                        criteriaBuilder.equal(root.get("isDeleted"), false),
                        criteriaBuilder.equal(root.get("deletedAt"), 0L));
            }

            // TRƯỜNG HỢP 2: Nếu filter != null
            List<Predicate> predicates = new ArrayList<>();

            // 1. Lọc theo tên (Name) - Tìm kiếm gần đúng (Like) và không phân biệt hoa thường
            if (filter.getName() != null && !filter.getName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                            "%" + filter.getName().trim().toLowerCase() + "%"
                ));
            }

            if (filter.getStreet() != null && !filter.getStreet().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("street")), 
                            "%" + filter.getStreet().trim().toLowerCase() + "%"));
            }

            if (filter.getWard() != null && !filter.getWard().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("ward")), 
                            "%" + filter.getWard().trim().toLowerCase() + "%"));
                
            }

            if (filter.getDistrict() != null && !filter.getDistrict().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("district")), 
                            "%" + filter.getDistrict().trim().toLowerCase() + "%"));
            }

            if (filter.getCity() != null && !filter.getCity().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("city")), 
                            "%" + filter.getCity().trim().toLowerCase() + "%"));
            }

            if (filter.getCountry() != null && !filter.getCountry().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("country")), 
                            "%" + filter.getCountry().toLowerCase() + "%"));
            }

            System.out.println("Active at Specification NOTNULL: " + active);
            predicates.add(criteriaBuilder.equal(root.get("inActive"), active));

            // 4. Luôn áp dụng điều kiện chưa bị xóa (Soft Delete)
            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), false));
            predicates.add(criteriaBuilder.equal(root.get("deletedAt"), 0L));


            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Warehouse> getAllWarehouseTrashFilter(WarehouseRequest filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Mặc định luôn lọc isDeleted = false


            return predicates.isEmpty() ? criteriaBuilder.conjunction()
                    : criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
