package com.CRM.response.TransferTicket.ConfirmReceipt;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReceiptItemResponse {

    private String skuCode;

    private String productName;

    private List<ReceiptItemColorResponse> colorDetails;
}
