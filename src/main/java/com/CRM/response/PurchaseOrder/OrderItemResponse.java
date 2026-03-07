package com.CRM.response.PurchaseOrder;

import java.math.BigDecimal;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class OrderItemResponse {
    private String productName;      // Dùng nếu là sản phẩm mới cần tạo detail
    private BigDecimal unitPrice;    // Giá nhập từ NCC đợt này
    private Double taxRate;          // Thuế
    private Integer quantityOrdered; // Số lượng đặt
    private Integer quantityReceived; // Số lượng thực tế nhà cung cấp giao đến
}
