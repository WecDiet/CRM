package com.CRM.repository.Specification;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import com.CRM.model.Supplier;
import com.CRM.request.Supplier.SupplierFilterRequest;

public class SupplierSpecification {

    public static Specification<Supplier> deleteThreshold(long threshold) {
        return BaseSpecification.deleteThreshold(threshold);
    }

    public static Specification<Supplier> warningThreshold(long threshold) {
        return BaseSpecification.warningThreshold(threshold);
    }


    public static Specification<Supplier> getAllSupplierFilter(SupplierFilterRequest filter, boolean active){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getName() != null && !filter.getName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + filter.getName().trim().toLowerCase() + "%"));
            }

            if (filter.getEmail() != null && !filter.getEmail().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + filter.getEmail().trim().toLowerCase() + "%"));
            }

            if (filter.getPhone() != null && !filter.getPhone().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("phone")), "%" + filter.getPhone().trim() + "%"));
            }

            if (filter.getSupplierCode() != null && !filter.getSupplierCode().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("supplierCode")), "%" + filter.getSupplierCode().trim() + "%"));
            }

            if (filter.getStreet() != null && !filter.getStreet().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("street")), "%" + filter.getStreet().toLowerCase() + "%"));
            }

            if (filter.getWard() != null && !filter.getWard().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("ward")), "%" + filter.getWard().toLowerCase() + "%"));
            }

            if (filter.getDistrict() != null && !filter.getDistrict().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("district")), "%" + filter.getDistrict().toLowerCase() + "%"));
            }

            if (filter.getCity() != null && !filter.getCity().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("city")), "%" + filter.getCity().toLowerCase() + "%"));
            }

            if (filter.getCountry() != null && !filter.getCountry().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("country")), "%" + filter.getCountry().toLowerCase() + "%"));
            }

            if (filter.getRating() != null) {
                predicates.add(criteriaBuilder.equal(root.get("rating"), filter.getRating()));
            }

            predicates.add(criteriaBuilder.equal(root.get("inActive"), active));
            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), false));
            predicates.add(criteriaBuilder.equal(root.get("deletedAt"), 0L));


            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }


    public static Specification<Supplier> getAllSupplierTrashFilter(SupplierFilterRequest filter){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getName() != null && !filter.getName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + filter.getName().trim().toLowerCase() + "%"));
            }

            if (filter.getEmail() != null && !filter.getEmail().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + filter.getEmail().trim().toLowerCase() + "%"));
            }

            if (filter.getPhone() != null && !filter.getPhone().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("phone")), "%" + filter.getPhone().trim() + "%"));
            }

            if (filter.getSupplierCode() != null && !filter.getSupplierCode().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("supplierCode")), "%" + filter.getSupplierCode().trim() + "%"));
            }

            if (filter.getStreet() != null && !filter.getStreet().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("street")), "%" + filter.getStreet().toLowerCase() + "%"));
            }

            if (filter.getWard() != null && !filter.getWard().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("ward")), "%" + filter.getWard().toLowerCase() + "%"));
            }

            if (filter.getDistrict() != null && !filter.getDistrict().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("district")), "%" + filter.getDistrict().toLowerCase() + "%"));
            }

            if (filter.getCity() != null && !filter.getCity().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("city")), "%" + filter.getCity().toLowerCase() + "%"));
            }

            if (filter.getCountry() != null && !filter.getCountry().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("country")), "%" + filter.getCountry().toLowerCase() + "%"));
            }

            if (filter.getRating() != null) {
                predicates.add(criteriaBuilder.equal(root.get("rating"), filter.getRating()));
            }

            predicates.add(criteriaBuilder.equal(root.get("inActive"), false));
            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), true));
            predicates.add(criteriaBuilder.greaterThan(root.get("deletedAt"), 0L));


            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
