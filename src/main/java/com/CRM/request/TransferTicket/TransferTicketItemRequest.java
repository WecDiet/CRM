package com.CRM.request.TransferTicket;

import java.util.List;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class TransferTicketItemRequest {

    private String skuCode;

    private String productName;

    private List<TransferTicketColorRequest> colors;

}
