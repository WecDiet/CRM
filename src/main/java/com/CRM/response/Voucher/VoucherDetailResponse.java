package com.CRM.response.Voucher;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.CRM.response.Product.BaseProductResponse;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class VoucherDetailResponse {
    private UUID id;
    private String name;
    private String code;
    private BigDecimal discount;
    private Integer quantity;
    private boolean isGlobal;
    private LocalDateTime startDate;
    private LocalDateTime expirationDate;
    private Integer usedCount = 0; 
    private String discountType; // "percentage" or "fixed"
    private boolean inActive;
    private BaseProductResponse products;
}
