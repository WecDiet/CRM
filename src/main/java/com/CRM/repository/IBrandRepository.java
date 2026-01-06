package com.CRM.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.CRM.model.Brand;

public interface IBrandRepository extends JpaRepository<Brand, UUID>, JpaSpecificationExecutor<Brand> {
    boolean existsByName(String name);

}
