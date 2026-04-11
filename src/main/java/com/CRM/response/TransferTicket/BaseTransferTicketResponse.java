package com.CRM.response.TransferTicket;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BaseTransferTicketResponse {

    private UUID id;
    
    private String ticketCode;

    private String storeName;
    
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate expectedDeliveryDate;
    
    private String status;
}
