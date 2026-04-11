package com.CRM.response.Inventory;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InventoryTransactionResponse {
    private UUID id;
    private UUID productId;
    private String productName;
    private String type;           // IN_PURCHASE, OUT_TRANSFER, IN_TRANSFER
    private Integer quantity;      // Dương (+) hoặc âm (-)
    private String locationName;
    private String locationType;
    private UUID referenceId;
    private String referenceCode;
}
