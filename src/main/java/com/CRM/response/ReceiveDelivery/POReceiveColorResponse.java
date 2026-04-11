package com.CRM.response.ReceiveDelivery;

import java.util.UUID;

import com.CRM.response.PatternColor.PatternColorItem;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class POReceiveColorResponse {
    
    private UUID id;

    private PatternColorItem patternColor;

    private Integer quantityOrdered;
 
    private Integer quantityReceived;

    /** Số lượng còn cần giao = quantityOrdered - quantityReceived */
    private Integer remainingQuantity;
 
    /** Trạng thái màu này đã nhận đủ chưa */
    private boolean fullyReceived;
}
