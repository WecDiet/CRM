package com.CRM.repository;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.CRM.model.PurchaseOrderColor;

public interface IPurchaseOrderColorRepository extends JpaRepository<PurchaseOrderColor, UUID> {
    
    @Modifying
    @Query("DELETE FROM PurchaseOrderColor poc WHERE poc.patternColor.id IN :colorIds")
    void deleteByPatternColorIdIn(@Param("colorIds") Set<UUID> colorIds);
}
