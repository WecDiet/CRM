package com.CRM.repository;

import java.util.Collection;
import java.util.List;
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
    
    List<Inventory> findAllByWarehouse(Warehouse warehouse);

    Optional<Inventory> findByProductAndWarehouse(Product product, Warehouse warehouse);

    @Query("""
        SELECT i FROM Inventory i
        WHERE i.warehouse.id = :warehouseId
        AND i.product.id IN :productIds
    """)
    List<Inventory> findAllByWarehouseIdAndProductIdIn(
        @Param("warehouseId") UUID warehouseId,
        @Param("productIds")  Collection<UUID> productIds
    );
}
