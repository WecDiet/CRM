package com.CRM.response.TransferTicket.ConfirmReceipt;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ComfirmReceiptResponse {

    private String ticketCode;

    private String warehouseName;

    private String storeName;

    private String status;

    private LocalDate expectedDeliveryDate;

    private String note;

    private List<ReceiptItemResponse> items;
}
