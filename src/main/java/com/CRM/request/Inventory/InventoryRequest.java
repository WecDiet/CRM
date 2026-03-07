package com.CRM.request.Inventory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryRequest {
    private int quantity;
    private String type; // Nhập kho, Xuất kho, Điều chuyển
    private String storeId;
    private String note;
}
