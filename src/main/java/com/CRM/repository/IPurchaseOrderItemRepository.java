package com.CRM.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.CRM.model.PurchaseOrderItem;

public interface IPurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, UUID> {
    Boolean existsByProduct_Id(UUID id);

    @Query("SELECT COUNT (poi) FROM PurchaseOrderItem poi WHERE poi.product.id = :productId AND poi.purchaseOrder.id <> :purchaseOrderId")
    long countByProductAndPurchaseOrder(UUID productId, UUID purchaseOrderId);
}
