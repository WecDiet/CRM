package com.CRM.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.CRM.model.Brand;

public interface IBrandRepository extends JpaRepository<Brand, Long>, JpaSpecificationExecutor<Brand> {
    boolean existsByName(String name);

}
