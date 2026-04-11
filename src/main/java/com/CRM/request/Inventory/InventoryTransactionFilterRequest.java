package com.CRM.request.Inventory;

import lombok.Data;

@Data
public class InventoryTransactionFilterRequest {
    
    private String referenceCode;

    private String productName;

    private String skuCode;

    private String warehouseName;
}
