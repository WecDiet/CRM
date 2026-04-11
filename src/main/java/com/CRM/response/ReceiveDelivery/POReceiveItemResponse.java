package com.CRM.response.ReceiveDelivery;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class POReceiveItemResponse {
    private UUID id;

    private String productId;
    private String productName;
    private String skuCode;


    private BigDecimal unitPrice;
    private Double taxRate;

     /** Tổng số lượng đặt */
    private Integer sumQuantityOrdered;

    /** Tổng đã nhận*/
    private Integer sumQuantityReceived;

    /** Tổng còn lại = quantityOrdered - quantityReceived */
    private Integer remainingQuantity;

    /** Trạng thái sản phẩm này đã nhận đủ tất cả màu chưa */
    private boolean fullyReceived;

    private List<POReceiveColorResponse> colors;
}
