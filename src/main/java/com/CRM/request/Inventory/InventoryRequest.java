package com.CRM.request.Inventory;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InventoryRequest {
    private int quantity;
    private String type; // Nhập kho, Xuất kho, Điều chuyển
    private String warehouseId;
    private String note;
}
