package com.CRM.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.CRM.model.PurchaseOrder;

public interface IPurchaseOrderRepository extends JpaRepository<PurchaseOrder, UUID>, JpaSpecificationExecutor<PurchaseOrder> {

    Optional<PurchaseOrder> findByPoNumber(String poCode);

    @Query("SELECT p FROM PurchaseOrder p WHERE LOWER(p.name) = LOWER(:name) AND p.isDeleted = false")
    Optional<PurchaseOrder> findActiveByName(@Param("name") String name);

    @Query("SELECT COUNT(p) > 0 FROM PurchaseOrder p WHERE p.name = :name AND p.isDeleted = false")
    boolean existsActiveByName(@Param("name") String name);

    @Query("""
        SELECT DISTINCT po FROM PurchaseOrder po
        LEFT JOIN FETCH po.items item
        LEFT JOIN FETCH item.colorDetails
        LEFT JOIN FETCH item.product
        WHERE po.poNumber = :poNumber
    """)
    Optional<PurchaseOrder> findByPONumberWithItems(@Param("poNumber") String poNumber);
}
