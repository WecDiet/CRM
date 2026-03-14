package com.CRM.request.PurchaseOrder.Delivery;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiveDeliveryRequest {
    private String deliveryNote;
 
    @Valid
    @NotEmpty(message = "Delivery must have at least one item.")
    private List<ReceivedItemRequest> items;
}
