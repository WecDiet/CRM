package com.CRM.request.Voucher;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class VoucherRequest {
    private String name;
    private String code;
    private String discountType;
    private BigDecimal discount;
    private BigDecimal maxDiscount;
    private boolean isGlobal;
    private LocalDateTime startDate;
    private LocalDateTime expirationDate;
}
