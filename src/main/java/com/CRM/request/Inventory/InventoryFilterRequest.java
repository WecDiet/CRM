package com.CRM.request.Inventory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryFilterRequest {
    private String productName;
    private String type; // Nhập kho, Xuất kho, Điều chuyển
    private String referenceCode;
}
