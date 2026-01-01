package com.CRM.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.CRM.model.Banner;

@Repository
public interface IBannerRepository extends JpaRepository<Banner, Long>, JpaSpecificationExecutor<Banner> {
    boolean existsByTitle(String title);

}
