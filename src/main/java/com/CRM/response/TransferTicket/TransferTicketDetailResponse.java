package com.CRM.response.TransferTicket;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TransferTicketDetailResponse {
    
    private UUID id;
    
    private String ticketCode;

    private String warehouseName;

    private String storeName;
    
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate expectedDeliveryDate;
    
    private String status;

    private String note;

    private List<TransferItem> items;
}
