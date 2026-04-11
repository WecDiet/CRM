package com.CRM.response.Inventory;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class InventoryResponse {

    private UUID id;

    private UUID productId;

    private String productName;

    private String mainImage;

    private String locationName;   // Tên kho hoặc cửa hàng

    private String locationType;   // "WAREHOUSE" | "STORE"
    
    private Integer quantityOnHand;

}
