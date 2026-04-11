package com.CRM.repository.Specification;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

import org.springframework.data.jpa.domain.Specification;

import com.CRM.model.Store;
import com.CRM.model.TransferTicket;
import com.CRM.model.TransferTicketItem;
import com.CRM.model.Warehouse;
import com.CRM.request.TransferTicket.TransferTicketFilterRequest;
import jakarta.persistence.criteria.Predicate;

public class TransferTicketSpecification {

    public static Specification<TransferTicket> getAllTransferTicket(TransferTicketFilterRequest filter) {
    return (root, query, criteriaBuilder) -> {
        List<Predicate> predicates = new ArrayList<>();

        // 1. Khai báo các Join
        Join<TransferTicket, Warehouse> warehouseJoin = root.join("warehouse", JoinType.LEFT);
        Join<TransferTicket, Store> storeJoin = root.join("store", JoinType.LEFT);

        // 2. Xử lý FETCH để chống N+1 (Chỉ thực hiện khi không phải query count)
        if (Long.class != query.getResultType() && long.class != query.getResultType()) {
            // Fetch Warehouse và Store
            root.fetch("warehouse", JoinType.LEFT);
            root.fetch("store", JoinType.LEFT);
            
            // Fetch Items và Product bên trong Item
            Fetch<TransferTicket, TransferTicketItem> itemsFetch = root.fetch("items", JoinType.LEFT);
            itemsFetch.fetch("product", JoinType.LEFT);

            // Tránh bản ghi bị nhân bản khi join với @OneToMany (items)
            query.distinct(true);
        }

        // 3. Thêm các điều kiện lọc (Predicates) từ filter request
        if (filter != null) {
            // Ví dụ: Lọc theo Ticket Code
            if (filter.getTicketCode() != null && !filter.getTicketCode().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("ticketCode"), "%" + filter.getTicketCode() + "%"));
            }

            // Ví dụ: Lọc theo ID của Warehouse (Dùng biến warehouseJoin đã tạo ở trên)
            if (filter.getWarehouseId() != null) {
                predicates.add(criteriaBuilder.equal(warehouseJoin.get("id"), filter.getWarehouseId()));
            }

            // Ví dụ: Lọc theo ID của Store (Dùng biến storeJoin)
            if (filter.getStoreId() != null) {
                predicates.add(criteriaBuilder.equal(storeJoin.get("id"), filter.getStoreId()));
            }

            // Ví dụ: Lọc theo Status
            if (filter.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), filter.getStatus()));
            }
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };


    }
}
