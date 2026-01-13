package com.CRM.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.CRM.model.Banner;

@Repository
public interface IBannerRepository extends JpaRepository<Banner, UUID>, JpaSpecificationExecutor<Banner> {
    boolean existsByTitle(String title);

    @Query("SELECT b FROM Banner b WHERE LOWER(b.title) = LOWER(:title) AND b.isDeleted = false")
    Optional<Banner> findActiveByTitle(@Param("title") String title);

    @Query("SELECT COUNT(b) > 0 FROM Banner b WHERE b.title = :title AND b.isDeleted = false")
    boolean existsActiveByTitle(@Param("title") String title);

}
