package com.CRM.repository.Specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

import com.CRM.model.Inventory;
import com.CRM.model.Product;
import com.CRM.model.ProductDetail;
import com.CRM.request.Inventory.InventoryFilterRequest;
import com.CRM.request.Product.ProductFilter;

public class ProductSpecification {

    public static Specification<Product> getAllProductFilter(ProductFilter filter, boolean active) {
        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            // 1️⃣ Tìm theo tên sản phẩm (LIKE)
            if (filter.getName() != null && !filter.getName().isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("name")),
                                "%" + filter.getName().toLowerCase() + "%"));
            }

            // 2️⃣ Giá tối thiểu
            if (filter.getMinPrice() != null) {
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(root.get("price"), filter.getMinPrice()));
            }

            // 3️⃣ Giá tối đa
            if (filter.getMaxPrice() != null) {
                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(root.get("price"), filter.getMaxPrice()));
            }

            // 6️⃣ Còn hàng hay không
            if (filter.getInStock() != null) {
                if (Boolean.TRUE.equals(filter.getInStock())) {
                    predicates.add(
                            criteriaBuilder.greaterThan(root.get("quantity"), 0));
                } else {
                    predicates.add(
                            criteriaBuilder.equal(root.get("quantity"), 0));
                }
            }

            // 7️⃣ Đánh giá tối thiểu
            if (filter.getMinRating() != null) {
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), filter.getMinRating()));
            }


            predicates.add(criteriaBuilder.equal(root.get("inActive"), active));
            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), false));
            predicates.add(criteriaBuilder.equal(root.get("deletedAt"), 0L));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }


    public static Specification<Product> deleteThreshold(long threshold) {
        return BaseSpecification.deleteThreshold(threshold);
    }

    public static Specification<Product> warningThreshold(long threshold) {
        return BaseSpecification.warningThreshold(threshold);
    }


    public static Specification<Product> getAllProductTrashFilter(ProductFilter filter){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1️⃣ Tìm theo tên sản phẩm (LIKE)
            if (filter.getName() != null && !filter.getName().isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("name")),
                                "%" + filter.getName().toLowerCase() + "%"));
            }

            // 2️⃣ Giá tối thiểu
            if (filter.getMinPrice() != null) {
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(root.get("price"), filter.getMinPrice()));
            }

            // 3️⃣ Giá tối đa
            if (filter.getMaxPrice() != null) {
                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(root.get("price"), filter.getMaxPrice()));
            }

            // 6️⃣ Còn hàng hay không
            if (filter.getInStock() != null) {
                if (Boolean.TRUE.equals(filter.getInStock())) {
                    predicates.add(
                            criteriaBuilder.greaterThan(root.get("quantity"), 0));
                } else {
                    predicates.add(
                            criteriaBuilder.equal(root.get("quantity"), 0));
                }
            }

            // 7️⃣ Đánh giá tối thiểu
            if (filter.getMinRating() != null) {
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), filter.getMinRating()));
            }

            predicates.add(criteriaBuilder.equal(root.get("inActive"), false));
            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), true));
            predicates.add(criteriaBuilder.greaterThan(root.get("deletedAt"), 0L));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }


    public static Specification<Product> getAllProductInInventory(InventoryFilterRequest filter){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<Product, Inventory> inventoryJoin = root.join("inventories", JoinType.LEFT);
            // 1️⃣ Tìm theo tên sản phẩm (LIKE)
            if (filter.getProductName() != null && !filter.getProductName().isEmpty()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("productDetail").get("name")),
                                "%" + filter.getProductName().toLowerCase() + "%"));
            }

            // if (filter.getReferenceCode() != null && !filter.getReferenceCode().isEmpty()) {
            //     predicates.add(criteriaBuilder.equal(inventoryJoin.get("referenceCode"), filter.getReferenceCode()));
            // }

            // if (filter.getType() != null && !filter.getType().isEmpty()) {
            //     predicates.add(criteriaBuilder.equal(inventoryJoin.get("type"), filter.getType()));
            // }

            predicates.add(criteriaBuilder.equal(root.get("status"), false));
            predicates.add(criteriaBuilder.equal(root.get("inActive"), true));
            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), false));
            predicates.add(criteriaBuilder.equal(root.get("deletedAt"), 0L));

            // 5. Đảm bảo kết quả không bị trùng lặp do JOIN
            query.distinct(true);

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }


    public static Specification<Product> getProductsForNewTicket(String keyword){
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            System.out.println("Keyword in Specification: " + keyword);
            List<Predicate> predicates = new ArrayList<>();

            // String likePattern = "%" + keyword.toLowerCase().trim() + "%";


            Join<Product, ProductDetail> productDetailJoin = root.join("productDetail", JoinType.LEFT);

            if (keyword != null && !keyword.isBlank()) {
                System.out.println("Joining Product and ProductDetail for keyword search: " + keyword);
                predicates.add(
                        criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("skuCode")), "%" + keyword.toLowerCase().trim() + "%"),
                                criteriaBuilder.like(criteriaBuilder.lower(productDetailJoin.get("name")), "%" + keyword.toLowerCase().trim() + "%")
                        )
                        
                    );
                System.out.println("Added predicates for SKU code and Product name with keyword: " + keyword);
                predicates.add(criteriaBuilder.equal(root.get("inActive"), true));
                predicates.add(criteriaBuilder.equal(root.get("isDeleted"), false));
                predicates.add(criteriaBuilder.equal(root.get("deletedAt"), 0L));

            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
