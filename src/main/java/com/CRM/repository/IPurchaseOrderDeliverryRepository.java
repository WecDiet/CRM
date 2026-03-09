package com.CRM.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.CRM.model.PurchaseOrderDelivery;

public interface IPurchaseOrderDeliverryRepository extends JpaRepository<PurchaseOrderDelivery, UUID>{
    
}
