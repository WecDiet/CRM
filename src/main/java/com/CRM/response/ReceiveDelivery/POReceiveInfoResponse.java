package com.CRM.response.ReceiveDelivery;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class POReceiveInfoResponse {
    
    private UUID id;
    private String name;

    private String supplierName;
    private String warehouseName;

    private LocalDate orderDate;
    private LocalDate expectedDeliveryDate;
    private String note;

    private List<POReceiveItemResponse> items;
}
