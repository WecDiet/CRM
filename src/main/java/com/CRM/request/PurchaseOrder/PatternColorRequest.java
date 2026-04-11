package com.CRM.request.PurchaseOrder;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class PatternColorRequest {

    private String patternColorId;
    
    private String lensColor;

    private String lensColorName;

    private String frameColor;

    private String frameColorName;

    private Integer quantityOrdered;
}
