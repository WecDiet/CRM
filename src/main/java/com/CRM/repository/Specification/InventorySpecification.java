package com.CRM.repository.Specification;

import org.springframework.data.jpa.domain.Specification;

import com.CRM.model.Inventory;

public class InventorySpecification {
    public static Specification<Inventory> getAllInventory(){
        return BaseSpecification.getAll();
    }
}
