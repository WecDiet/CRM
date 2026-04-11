package com.CRM.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "po_items")
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

    @Column(name = "product_name", nullable = false)
    private String productName; // Tên sản phẩm (lưu trực tiếp để tránh phụ thuộc vào bảng Product)
    
    @Column(name = "unit_price")
    private BigDecimal unitPrice;     // Giá nhập từ NCC trên 1 đơn vị sản phẩm
    
    @Column(name = "tax_rate")
    private Double taxRate;           // Thuế suất (VD: 0.1 cho 10%)

    // @Column(name = "quantity_ordered")
    // private Integer quantityOrdered;  // Số lượng đặt mua

    // @Column(name = "quantity_received")
    // private Integer quantityReceived; // Số lượng thực tế nhà cung cấp giao đến

    @OneToMany(mappedBy = "purchaseOrderItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PurchaseOrderColor> colorDetails = new HashSet<>();

    /**
     * Tổng số lượng đặt mua = SUM(colorDetails.quantityOrdered).
     * Tính động — không lưu DB, không bao giờ lệch với colorDetails.
     */
    @Transient
    public int getQuantityOrdered() {
        return colorDetails.stream()
                .mapToInt(c -> c.getQuantityOrdered() != null ? c.getQuantityOrdered() : 0)
                .sum();
    }
 
    /**
     * Tổng số lượng đã nhận = SUM(colorDetails.quantityReceived).
     * Tính động — không lưu DB, không bao giờ lệch với colorDetails.
     */
    @Transient
    public int getQuantityReceived() {
        return colorDetails.stream()
                .mapToInt(c -> c.getQuantityReceived() != null ? c.getQuantityReceived() : 0)
                .sum();
    }
 
    /**
     * Tiện ích kiểm tra đã nhận đủ hàng chưa.
     */
    @Transient
    public boolean isFullyReceived() {
        return !colorDetails.isEmpty() && getQuantityReceived() >= getQuantityOrdered();
    }
}
