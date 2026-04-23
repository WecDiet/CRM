package com.CRM.request.TransferTicket.ConfirmReceipt;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class ReceivedTransferItemRequest {
    private String skuCode;

    @JsonProperty("colors")
    private List<ColorReceiptRequest> colors;
}
