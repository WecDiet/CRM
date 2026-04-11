package com.CRM.request.PurchaseOrder;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class PurchaseOrderRequest {
    private String name;
    private String warehouseId;
    private String supplierId;
    private String status;
    private String note;

    private LocalDate orderDate;

    private LocalDate expectedDeliveryDate;

    private List<OrderItemRequest> items; // Danh sách sản phẩm trong đơn
}
