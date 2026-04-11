package com.CRM.request.TransferTicket;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class TransferTicketRequest {

    private String ticketCode;

    private String warehouseId;

    private String storeId;

    private String status;

    private LocalDate expectedDeliveryDate; // Ngày dự kiến hàng tới cửa hàng

    private String note;

    private List<TransferTicketItemRequest> items;
}
