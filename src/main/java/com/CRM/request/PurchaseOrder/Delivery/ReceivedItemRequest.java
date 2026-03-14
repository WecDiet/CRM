package com.CRM.request.PurchaseOrder.Delivery;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceivedItemRequest {
    
    @NotBlank(message = "PO item id is required.")
    private String poItemId; // PurchaseOrderItem.id
     
    @NotNull
    @Positive(message = "Quantity delivered must be greater than 0.")
    private Integer quantityDelivered; // Số lượng giao LẦN NÀY
}
