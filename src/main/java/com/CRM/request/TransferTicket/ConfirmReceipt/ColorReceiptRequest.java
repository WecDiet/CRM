package com.CRM.request.TransferTicket.ConfirmReceipt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColorReceiptRequest {
    private String colorId;

    private Integer quantityReceived;
}
