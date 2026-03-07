package com.CRM.request.PurchaseOrder;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class PurchaseOrderFilterRequest {
    private String name;
    private String supplierName;
    private String warehouseName;
    private String status;

    private Integer orderDay;
    private Integer orderMonth;
    private Integer orderYear;
}
