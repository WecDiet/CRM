package com.CRM.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.CRM.model.PurchaseOrderItem;

public interface IPurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, UUID> {
    
}
