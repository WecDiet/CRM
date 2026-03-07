package com.CRM.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum PurchaseOrderEnum {
    DRAFT, // Nháp
    ORDERED, // Đã đặt hàng
    PARTIALLY_RECEIVED, // Nhận một phần
    COMPLETED, // Đã nhập kho đủ
    CANCELLED; // Hủy

    String status;

    public static String getStatusName(String statusName){
        try {
            return PurchaseOrderEnum.valueOf(statusName.toUpperCase()).getStatus();
        } catch (IllegalArgumentException e) {
            return "unknown";
        }
    }

}
