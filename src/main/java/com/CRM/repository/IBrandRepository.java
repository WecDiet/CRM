package com.CRM.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.CRM.model.Brand;

public interface IBrandRepository extends JpaRepository<Brand, UUID>, JpaSpecificationExecutor<Brand> {
        boolean existsByName(String name);

        @Query("SELECT b FROM Brand b WHERE LOWER(b.name) = LOWER(:name) AND b.isDeleted = false")
        Optional<Brand> findActiveByName(@Param("name") String name);

        // @Query("""
        // SELECT COUNT(b) > 0
        // FROM Brand b
        // WHERE b.name = :name
        // AND b.category.id = :categoryId
        // AND b.isDeleted = false
        // """)
        // boolean existsActiveByNameAndCategory(@Param("name") String name,
        // @Param("categoryId") UUID categoryId);

        @Query("SELECT COUNT(b) > 0 FROM Brand b WHERE b.name = :name AND b.isDeleted = false")
        boolean existsActiveByName(@Param("name") String name);

}
