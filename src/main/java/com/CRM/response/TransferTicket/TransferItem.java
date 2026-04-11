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
public class TransferItem {

    private UUID id;

    private String productName;

    private Integer quantitySent;

    private Integer quantityReceived; 

    private List<PatternColorItem> colors;
}
