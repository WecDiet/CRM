package com.CRM.repository.Specification;


import org.springframework.data.jpa.domain.Specification;

import com.CRM.model.Inventory;

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

}
