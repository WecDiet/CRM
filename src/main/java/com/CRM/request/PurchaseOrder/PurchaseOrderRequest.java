package com.CRM.request.PurchaseOrder;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class PurchaseOrderRequest {
    private String name;
    private String warehouseId;
    private String supplierId;
    private String status;
    private String description;
    
    // Đơn giản hóa việc truyền ngày tháng bằng chuỗi chuẩn yyyy-MM-dd hoặc truyền trực tiếp Year/Month/Day như code cũ của bạn
    private Integer orderDay;
    private Integer orderMonth;
    private Integer orderYear;
    
    private Integer expectedDeliveryDay;
    private Integer expectedDeliveryMonth;
    private Integer expectedDeliveryYear;

    private List<OrderItemRequest> items; // Danh sách sản phẩm trong đơn
}
