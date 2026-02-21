package com.CRM.model;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "purchase_order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Kết nối với đơn mua hàng cha (PO)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "po_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    // Sản phẩm cụ thể được nhập về
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;     // Giá nhập từ NCC trên 1 đơn vị sản phẩm
    
    @Column(name = "tax_rate")
    private Double taxRate;           // Thuế suất (VD: 0.1 cho 10%)

    @Column(name = "quantity_ordered")
    private Integer quantityOrdered;  // Số lượng đặt mua

    @Column(name = "quantity_received")
    private Integer quantityReceived; // Số lượng thực tế nhà cung cấp giao đến

}
