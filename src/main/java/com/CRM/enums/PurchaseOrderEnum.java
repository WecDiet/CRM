package com.CRM.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public enum PurchaseOrderEnum {

    DRAFT("DRAFT"),                 // Nháp
    ORDERED("ORDERED"),             // Đã đặt hàng
    PARTIALLY_RECEIVED("PARTIALLY_RECEIVED"), // Nhận một phần
    COMPLETED("COMPLETED"),         // Đã nhập kho đủ
    CANCELLED("CANCELLED");         // Hủy

    private final String status;

    public static PurchaseOrderEnum fromString(String status) {
        try {
            return PurchaseOrderEnum.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid purchase order status: " + status);
        }
    }
}
