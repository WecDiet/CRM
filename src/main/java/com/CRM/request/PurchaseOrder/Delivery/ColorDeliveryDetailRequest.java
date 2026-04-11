package com.CRM.request.PurchaseOrder.Delivery;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ColorDeliveryDetailRequest {
    
    @NotBlank(message = "poItemColorId không được để trống")
    private String poItemColorId;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng không được âm")
    private Integer quantityDelivered;
}
