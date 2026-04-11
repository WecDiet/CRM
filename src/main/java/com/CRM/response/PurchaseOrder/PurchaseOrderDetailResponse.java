package com.CRM.response.PurchaseOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PurchaseOrderDetailResponse {
    private String name;
    private String poNumber;
    private String status;
    
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate orderDate;
    
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate expectedDeliveryDate;
    
    private List<OrderItemResponse> items; // Danh sách sản phẩm trong đơn
}
