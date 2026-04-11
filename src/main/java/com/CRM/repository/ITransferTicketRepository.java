package com.CRM.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.CRM.model.TransferTicket;

public interface ITransferTicketRepository extends JpaRepository<TransferTicket, UUID>, JpaSpecificationExecutor<TransferTicket> {
    
    Optional<TransferTicket> findByTicketCode(String ticketCode);
}
