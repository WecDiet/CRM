package com.CRM.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
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
import jakarta.persistence.OneToOne;
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

    @Column(name ="name", nullable = false)
    private String name; // Tên đơn mua hàng (VD: Đơn mua hàng tháng 9/2023)

    @Column(name = "po_number")
    private String poNumber; // Mã đơn mua hàng (VD: PO-2023-0001)

    @ManyToOne 
    @JoinColumn(name = "supplier_id")
    private Supplier supplier; // Kết nối NCC

    @ManyToOne 
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse; // Luôn trỏ tới Kho Tổng

    // DRAFT (Nháp), 
    // ORDERED (Đã đặt hàng), 
    // PARTIALLY_RECEIVED (Nhận một phần), 
    // CANCELLED (Hủy)
    @Column(name = "status") 
    private String status;     

    @Column(name = "total_amount")
    private BigDecimal totalAmount; // Tổng giá trị đơn hàng nhập

    private LocalDate orderDate;
    
    private LocalDate expectedDeliveryDate; // Ngày dự kiến hàng về

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "purchaseOrder",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<PurchaseOrderItem> items = new ArrayList<>();

    @OneToOne(mappedBy = "purchaseOrder", cascade = CascadeType.ALL)
    private PurchaseOrderDelivery purchaseOrderDelivery;
}
