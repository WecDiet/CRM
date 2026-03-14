package com.CRM.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.CRM.model.Inventory;
import com.CRM.model.Product;
import com.CRM.model.Warehouse;

import jakarta.persistence.LockModeType;

public interface IInventoryRepository extends JpaRepository<Inventory, UUID>, JpaSpecificationExecutor<Inventory> {
    Boolean existsByProduct_Id(UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT i FROM Inventory i WHERE i.product = :product AND i.warehouse = :warehouse
            """)
    Optional<Inventory> findByProductAndWarehouseWithLock(@Param("product") Product product, @Param("warehouse") Warehouse warehouse);
    
}
