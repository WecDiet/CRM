package com.CRM.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "purchase_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrder extends BaseEntity {
    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "po_number")
    private String poNumber; // Mã đơn mua hàng (VD: PO-2023-0001)

    @ManyToOne 
    @JoinColumn(name = "supplier_id")
    private Supplier supplier; // Kết nối NCC

    @ManyToOne 
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse; // Luôn trỏ tới Kho Tổng

    @Column(name = "status")
    private String status;     
    // DRAFT (Nháp), ORDERED (Đã đặt hàng), 
    // PARTIALLY_RECEIVED (Nhận một phần), 
    // COMPLETED (Đã nhập kho đủ), CANCELLED (Hủy)

    @Column(name = "total_amount")
    private BigDecimal totalAmount; // Tổng giá trị đơn hàng nhập

    private LocalDateTime orderDate;
    
    private LocalDateTime expectedDeliveryDate; // Ngày dự kiến hàng về

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL)
    private List<PurchaseOrderItem> items;
}
