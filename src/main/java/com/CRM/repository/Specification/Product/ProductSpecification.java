package com.CRM.repository.Specification.Product;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import com.CRM.model.Product;
import com.CRM.request.Product.ProductFilter;

public class ProductSpecification {

    public static Specification<Product> getAllProductFilter(ProductFilter filter) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // 1️⃣ Tìm theo tên sản phẩm (LIKE)
            if (filter.getName() != null && !filter.getName().isBlank()) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("name")),
                                "%" + filter.getName().toLowerCase() + "%"));
            }

            // 2️⃣ Giá tối thiểu
            if (filter.getMinPrice() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("price"), filter.getMinPrice()));
            }

            // 3️⃣ Giá tối đa
            if (filter.getMaxPrice() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(root.get("price"), filter.getMaxPrice()));
            }

            // 6️⃣ Còn hàng hay không
            if (filter.getInStock() != null) {
                if (Boolean.TRUE.equals(filter.getInStock())) {
                    predicates.add(
                            cb.greaterThan(root.get("quantity"), 0));
                } else {
                    predicates.add(
                            cb.equal(root.get("quantity"), 0));
                }
            }

            // 7️⃣ Đánh giá tối thiểu
            if (filter.getMinRating() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("rating"), filter.getMinRating()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
