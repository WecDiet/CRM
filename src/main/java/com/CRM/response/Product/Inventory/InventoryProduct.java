package com.CRM.response.Product.Inventory;

import java.util.UUID;

import com.CRM.response.Inventory.InventoryQuantity;
import com.CRM.response.Media.MediaResponse;
import com.CRM.response.Product.BaseProductDetail;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
public class InventoryProduct {
    private UUID id;
    private String skuCode;
    private MediaResponse mainImage;
    private BaseProductDetail productDetail;
    private Boolean status;

    private InventoryQuantity inventories;
}
