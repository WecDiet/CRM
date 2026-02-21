package com.CRM.request.Voucher;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherFilterRequest {
    private String name;
    private String code;
    private String discountType;
    private BigDecimal discount;
    private Boolean isGlobal;
    
    // start date
    private Integer startDay;
    private Integer startMonth;
    private Integer startYear;

    // expiration date
    private Integer endDay;
    private Integer endMonth;
    private Integer endYear;
}
