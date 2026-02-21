package com.CRM.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.CRM.model.Store;

public interface IStoreRepository extends JpaRepository<Store, UUID>, JpaSpecificationExecutor<Store>{
    
}
