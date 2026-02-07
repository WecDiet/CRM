package com.CRM.response.Inventory;

import java.util.UUID;

import com.CRM.response.Product.BasicProductResponse;
import com.CRM.response.Warehouse.WarehouseResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {
    private UUID id;
    private int quantity;
    private String type; // Nhập kho, Xuất kho, Điều chuyển
    private String referenceCode; // Mã tham chiếu liên quan đến giao dịch kho hàng (nếu có) 
    private String note;
    private BasicProductResponse product;
    private WarehouseResponse warehouse;
}
