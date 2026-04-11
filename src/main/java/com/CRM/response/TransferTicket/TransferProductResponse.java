package com.CRM.response.TransferTicket;

import java.util.List;
import java.util.UUID;

import com.CRM.response.PatternColor.PatternColorItem;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TransferProductResponse {
    private UUID id;
    private String skuCode;
    private String productName;

    private List<PatternColorItem> colors;
}
