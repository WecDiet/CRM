package com.CRM.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.CRM.model.Category;

public interface ICategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {
}
