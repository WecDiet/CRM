package com.CRM.request.PurchaseOrder.Delivery;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceivedItemRequest {
    
    private String skuCode;
 
    /**
     * Danh sách màu và số lượng thực tế nhận được trong lần này.
     * Chỉ truyền màu nào có hàng về — không cần truyền màu không giao lần này.
     */
    @NotEmpty(message = "Phải có ít nhất 1 màu trong lần giao")
    @Valid
    private List<ColorDeliveryDetailRequest> colors;
}
