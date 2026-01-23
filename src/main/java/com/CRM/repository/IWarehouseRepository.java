package com.CRM.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.CRM.model.Role;
import com.CRM.model.Warehouse;

public interface IWarehouseRepository extends JpaRepository<Warehouse, UUID>, JpaSpecificationExecutor<Warehouse> {

    boolean existsByName(String name);

    boolean existsByCode(String code);

    // Tối ưu việc tên Hoa và Thường
    @Query("SELECT w FROM Warehouse w WHERE LOWER(w.name) = LOWER(:name) AND w.isDeleted = false")
    Optional<Warehouse> findActiveByName(@Param("name") String name);

    @Query("SELECT COUNT(w) > 0 FROM Warehouse w WHERE w.name = :name AND w.isDeleted = false")
    boolean existsActiveByName(@Param("name") String name);
}
