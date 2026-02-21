package com.CRM.response.Voucher;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class VoucherResponse {
    private UUID id;
    private String code;
    private BigDecimal discount;
    private LocalDateTime startDate;
    private LocalDateTime expirationDate;
    private boolean inActive;
}
