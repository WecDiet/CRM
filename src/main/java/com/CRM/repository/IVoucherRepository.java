package com.CRM.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.CRM.model.Voucher;

public interface IVoucherRepository extends JpaRepository<Voucher, UUID>, JpaSpecificationExecutor<Voucher> {
    
}
