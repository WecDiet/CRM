package com.CRM.response.TransferTicket.ConfirmReceipt;

import com.CRM.response.PatternColor.PatternColorItem;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReceiptItemColorResponse {
    
    private PatternColorItem patternColor; // null = không phân biệt màu

    private Integer quantitySent;
}
