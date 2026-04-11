package com.CRM.request.TransferTicket;

import lombok.Data;

@Data
public class TransferTicketFilterRequest {

    private String status;
    
    private String ticketCode;

    private String storeId;

    private String warehouseId;
}
