package com.CRM.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.CRM.model.Supplier;

public interface ISupplierRepository extends JpaRepository<Supplier, UUID>, JpaSpecificationExecutor<Supplier> {
    Boolean existsByTaxCode(String taxCode);

    @Query("SELECT s FROM Supplier s WHERE LOWER(s.name) = LOWER(:name) AND s.isDeleted = false")
    Optional<Supplier> findActiveByName(@Param("name") String name);

    @Query("SELECT COUNT(s) > 0 FROM Supplier s WHERE s.name = :name AND s.isDeleted = false")
    boolean existsActiveByName(@Param("name") String name);
}
