package com.CRM.response.Inventory;

import java.util.UUID;

import com.CRM.response.Product.BaseProductResponse;
import com.CRM.response.Warehouse.WarehouseResponse;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class InventoryResponse {
    private UUID id;
    private int quantity;
    private String type; // Nhập kho, Xuất kho, Điều chuyển
    private String referenceCode; // Mã tham chiếu liên quan đến giao dịch kho hàng (nếu có) 
    private String note;
    private BaseProductResponse product; 
    private WarehouseResponse warehouse;
}
