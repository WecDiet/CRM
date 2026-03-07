package com.CRM.response.PurchaseOrder;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PurchaseOrderResponse {
    private UUID id;
    private String name;
    private String poNumber;
    private String status;
    
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate orderDate;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate expectedDeliveryDate;
}
