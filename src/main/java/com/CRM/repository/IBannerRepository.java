package com.CRM.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.CRM.model.Banner;
import com.CRM.model.Brand;

@Repository
public interface IBannerRepository extends JpaRepository<Banner, UUID>, JpaSpecificationExecutor<Banner> {
    boolean existsByName(String name);

    @Query("SELECT b FROM Banner b WHERE LOWER(b.name) = LOWER(:name) AND b.isDeleted = false")
    Optional<Banner> findActiveByName(@Param("name") String name);

    @Query("SELECT COUNT(b) > 0 FROM Banner b WHERE b.name = :name AND b.isDeleted = false")
    boolean existsActiveByName(@Param("name") String name);

}
