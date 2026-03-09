package com.CRM.response.Inventory;

import java.util.UUID;
import com.CRM.response.Product.BaseProductDetail;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class InventoryProduct {
    private UUID id;
    private String skuCode;
    private BaseProductDetail productDetail;
    private Boolean status;
}
