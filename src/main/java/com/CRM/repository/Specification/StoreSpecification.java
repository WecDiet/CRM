package com.CRM.repository.Specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.CRM.model.Store;
import com.CRM.request.Store.StoreFilterRequest;
import jakarta.persistence.criteria.Predicate;

public class StoreSpecification {
    
    public static Specification<Store> getAllStore(StoreFilterRequest filter, boolean active){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter != null) {
                if (filter.getName() != null && !filter.getName().isEmpty()) {
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + filter.getName() + "%"));
                }

                if (filter.getStreet() != null && !filter.getStreet().isEmpty()) {
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("street")), "%" + filter.getStreet() + "%"));
                }

                if (filter.getWard() != null && !filter.getWard().isEmpty()) {
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("ward")), "%" + filter.getWard() + "%"));
                }

                if (filter.getDistrict() != null && !filter.getDistrict().isEmpty()) {
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("district")), "%" + filter.getDistrict() + "%"));
                }

                if (filter.getCity() != null && !filter.getCity().isEmpty()) {
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("city")), "%" + filter.getCity() + "%"));
                }

                if (filter.getCountry() != null && !filter.getCountry().isEmpty()) {
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("country")), "%" + filter.getCountry() + "%"));
                }
            }

            predicates.add(criteriaBuilder.equal(root.get("inActive"), active));
            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), false));
            predicates.add(criteriaBuilder.equal(root.get("deletedAt"), 0L));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

    }
}
