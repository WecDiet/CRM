package com.CRM.request.TransferTicket.ConfirmReceipt;

import java.util.ArrayList;
import java.util.List;

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
public class ConfirmTransferTicketRequest {
    private String note;

    @JsonProperty("receivedItems")
    private List<ReceivedTransferItemRequest> receivedItems = new ArrayList<>();
}
