package com.CRM.response.Inventory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InventoryQuantity {
    private Integer quantity;
    private String referenceCode;
}
