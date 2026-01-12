package com.CRM.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.CRM.model.Role;

public interface IRoleRepository extends JpaRepository<Role, UUID>, JpaSpecificationExecutor<Role> {
    boolean existsByName(String name);

    boolean existsByCode(String code);

    // @Query("SELECT r FROM roles r WHERE r.name = :name AND r.isDeleted = false")
    // Optional<Role> findActiveByName(@Param("name") String name);

    // Tối ưu việc tên Hoa và Thường
    @Query("SELECT r FROM Role r WHERE LOWER(r.name) = LOWER(:name) AND r.isDeleted = false")
    Optional<Role> findActiveByName(@Param("name") String name);

    @Query("SELECT COUNT(r) > 0 FROM Role r WHERE r.name = :name AND r.isDeleted = false")
    boolean existsActiveByName(@Param("name") String name);
}
