package com.CRM.repository.Specification;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.CRM.model.Inventory;
import com.CRM.model.InventoryTransaction;
import com.CRM.model.Product;
import com.CRM.model.ProductDetail;
import com.CRM.model.Warehouse;
import com.CRM.request.Inventory.InventoryFilterRequest;
import com.CRM.request.Inventory.InventoryTransactionFilterRequest;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class InventorySpecification {
    public static Specification<Inventory> getAllInventory(){
        return BaseSpecification.getAll();
    }

    public static Specification<Inventory> deleteThreshold(long threshold) {
        return BaseSpecification.deleteThreshold(threshold);
    }

    public static Specification<Inventory> warningThreshold(long threshold) {
        return BaseSpecification.warningThreshold(threshold);
    }

    public static Specification<Inventory> getAllProductWarehouse(String warehouseId, InventoryFilterRequest filter){
        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            Join<Inventory, Product> productJoin = root.join("product", JoinType.LEFT);

            Join<Product, ProductDetail> productDetailJoin = productJoin.join("productDetail", JoinType.LEFT);

            predicates.add(criteriaBuilder.equal(root.get("warehouse").get("id"), UUID.fromString(warehouseId)));

            if (filter.getProductName() != null && !filter.getProductName().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(productDetailJoin.get("name")), "%" + filter.getProductName().toLowerCase().trim() + '%'));
            }

            if (filter.getSkuCode() != null && !filter.getSkuCode().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(productJoin.get("skuCode")), "%" + filter.getSkuCode().trim() + "%"));
            }

            predicates.add(criteriaBuilder.equal(productJoin.get("inActive"), true));
            predicates.add(criteriaBuilder.equal(productJoin.get("isDeleted"), false));
            predicates.add(criteriaBuilder.equal(productJoin.get("deletedAt"), 0L));

            query.distinct(true);

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Inventory> getAllProductByStore(String storeId, InventoryFilterRequest filter){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<Inventory, Product> productJoin = root.join("product", JoinType.LEFT);

            Join<Product, ProductDetail> productDetailJoin = productJoin.join("productDetail", JoinType.LEFT);

            predicates.add(criteriaBuilder.equal(root.get("store").get("id"), UUID.fromString(storeId)));

            if (filter.getProductName() != null && !filter.getProductName().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(productDetailJoin.get("name")), "%" + filter.getProductName().toLowerCase().trim() + '%'));
            }

            if (filter.getSkuCode() != null && !filter.getSkuCode().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(productJoin.get("skuCode")), "%" + filter.getSkuCode().trim() + "%"));
            }

            predicates.add(criteriaBuilder.equal(productJoin.get("inActive"), true));
            predicates.add(criteriaBuilder.equal(productJoin.get("isDeleted"), false));
            predicates.add(criteriaBuilder.equal(productJoin.get("deletedAt"), 0L));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }


    public static Specification<InventoryTransaction> getAllInventoryTransaction(InventoryTransactionFilterRequest filter){
        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            Join<InventoryTransaction, Product> productJoin;
            Join<Product, ProductDetail> productDetailJoin;
            Join<InventoryTransaction, Warehouse> warehouseJoin;
            /*
                **** Long.class != query.getResultType()
                    - Phân biệt giữa query lấy dữ liệu (select entity) và query đếm số lượng (count)
                    - 

            */
            if (Long.class != query.getResultType()) {
                
                // Lấy dữ liệu: Dùng FETCH để gom hết vào 1 câu SQL duy nhất
                productJoin = root.join("product", JoinType.LEFT);
                productDetailJoin = productJoin.join("productDetail", JoinType.LEFT);
                warehouseJoin = root.join("warehouse", JoinType.LEFT);

                root.fetch("product", JoinType.LEFT).fetch("productDetail", JoinType.LEFT);
                root.fetch("warehouse", JoinType.LEFT);

                query.distinct(true);
            } else {

                // Đếm record (Count): Chỉ JOIN bình thường, không FETCH
                productJoin = root.join("product", JoinType.LEFT);
                productDetailJoin = productJoin.join("productDetail", JoinType.LEFT);
                warehouseJoin = root.join("warehouse", JoinType.LEFT);
            }

            // PO-455263754E394F8E-20260314/ Delivery #1 -> filer nhập PO-455263754E394F8E-20260314 -> search
            Expression<String> dataCode = criteriaBuilder.function(
                    "split_part",
                    String.class,
                    root.get("referenceCode"),
                    criteriaBuilder.literal("/").as(String.class),
                    criteriaBuilder.literal(1).as(String.class)
            );

            if (filter.getReferenceCode() != null && !filter.getReferenceCode().isEmpty()) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(dataCode), filter.getReferenceCode().trim().toLowerCase()));
            }

            if (filter.getSkuCode() != null && !filter.getSkuCode().isEmpty()) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(productJoin.get("skuCode")),  filter.getSkuCode().toLowerCase()));
            }

            if (filter.getProductName() != null && !filter.getProductName().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(productDetailJoin.get("name")), "%" +filter.getProductName() + "%"));
            }

            if (filter.getWarehouseName() != null && !filter.getWarehouseName().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(warehouseJoin.get("name")), "%" + filter.getWarehouseName() + "%"));
            }

            predicates.add(criteriaBuilder.equal(root.get("inActive"), true));
            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), false));
            predicates.add(criteriaBuilder.equal(root.get("deletedAt"), 0L));
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<InventoryTransaction> getAllTransactionWarehouse(String warehouseId, String productId, InventoryTransactionFilterRequest filter) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            Join<InventoryTransaction, Product> productJoin = root.join("product");
            Join<Product, ProductDetail> productDetailJoin = productJoin.join("productDetail");
            Join<InventoryTransaction, Warehouse> warehouseJoin = root.join("warehouse");

            // SPLIT_PART(reference_code, '/', 1)
            Expression<String> dataCode = criteriaBuilder.function(
                    "split_part",
                    String.class,
                    root.get("referenceCode"),
                    criteriaBuilder.literal("/").as(String.class),
                    criteriaBuilder.literal(1).as(String.class)
            );

            predicates.add(criteriaBuilder.equal(root.get("warehouse").get("id"), UUID.fromString(warehouseId)));

            predicates.add(criteriaBuilder.equal(root.get("product").get("id"), UUID.fromString(productId)));

            if (filter.getReferenceCode() != null && !filter.getReferenceCode().isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(dataCode),
                        filter.getReferenceCode().trim()
                ));
            }

            if (filter.getSkuCode() != null && !filter.getSkuCode().isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(productJoin.get("skuCode")),
                        filter.getSkuCode().toLowerCase()
                ));
            }

            if (filter.getProductName() != null && !filter.getProductName().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(productDetailJoin.get("name")),
                        "%" + filter.getProductName().toLowerCase() + "%"
                ));
            }

            if (filter.getWarehouseName() != null && !filter.getWarehouseName().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(warehouseJoin.get("name")),
                        "%" + filter.getWarehouseName().toLowerCase() + "%"
                ));
            }

            predicates.add(criteriaBuilder.isTrue(root.get("inActive")));
            predicates.add(criteriaBuilder.isFalse(root.get("isDeleted")));
            predicates.add(criteriaBuilder.equal(root.get("deletedAt"), 0L));

            query.distinct(true);

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
