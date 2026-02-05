package com.CRM.repository.Specification;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import com.CRM.model.Warehouse;
import com.CRM.request.Warehouse.WarehouseRequest;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;

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
                return criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("inActive"), true),
                        criteriaBuilder.equal(root.get("isDeleted"), false),
                        criteriaBuilder.equal(root.get("deletedAt"), 0L));
            }

            List<Predicate> predicates = new ArrayList<>();

            if (filter.getName() != null && !filter.getName().trim().isEmpty()) {
                predicates.add(hasFieldLike("name", filter.getName()));
            }
        };
    }

    public static Specification<Warehouse> getAllWarehouseTrashFilter(WarehouseRequest filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Mặc định luôn lọc isDeleted = false
            Specification<Warehouse> spec = Specification.where(isFieldEqual("isDeleted", true))
                    .and(isFieldEqual("deletedAt", 0L))
                    .and(isFieldEqual("inActive", false));
            if (filter != null) {
                spec = spec.and(hasFieldLike("name", filter.getName()))
                        .and(hasFieldLike("street", filter.getStreet()))
                        .and(hasFieldLike("ward", filter.getWard()))
                        .and(hasFieldLike("district", filter.getDistrict()))
                        .and(hasFieldLike("city", filter.getCity()))
                        .and(hasFieldLike("country", filter.getCountry()));
            }
            predicates.add(criteriaBuilder.equal(root.get("inActive"), false));
            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), true));
            predicates.add(criteriaBuilder.greaterThan(root.get("deletedAt"), 0L));

            return predicates.isEmpty() ? criteriaBuilder.conjunction()
                    : criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Warehouse> hasFieldLike(String fieldName, String value) {
        return (root, query, cb) -> {
            if (value == null || value.trim().isEmpty()) {
                return null; // Spring Data JPA sẽ bỏ qua filter này
            }
            return cb.like(cb.lower(root.get(fieldName)), "%" + value.trim().toLowerCase() + "%");
        };
    }

    // 2. Mảnh ghép lọc cố định (Equal)
    public static <T> Specification<Warehouse> isFieldEqual(String fieldName, T value) {
        return (root, query, cb) -> value == null ? null : cb.equal(root.get(fieldName), value);
    }

}
