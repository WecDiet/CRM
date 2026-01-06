package com.CRM.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.CRM.model.Role;

import jakarta.transaction.Transactional;

public interface IRoleRepository extends JpaRepository<Role, UUID>, JpaSpecificationExecutor<Role> {
    boolean existsByName(String name);

    boolean existsByCode(String code);

    // @Modifying
    // @Transactional
    // @Query("DELETE FROM roles r WHERE r.is_deleted = true AND r.in_active = true
    // AND r.deleted_at<=: threshold AND r.deletedAt>0")
    // int deleteExpireRoles(@Param("threshold") long threshold);
}
