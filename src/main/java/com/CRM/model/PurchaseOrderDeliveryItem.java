package com.CRM.model;

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
@Table(name = "po_delivery_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderDeliveryItem {
 
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id", nullable = false)
    private PurchaseOrderDelivery delivery;
 
    // Trỏ về dòng sản phẩm trong PO gốc để tính lũy kế
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "po_item_id", nullable = false)
    private PurchaseOrderItem purchaseOrderItem;

    /**
     * Trỏ về đúng dòng màu trong PO để tính lũy kế chính xác.
     * Nếu sản phẩm không phân biệt màu thì vẫn trỏ vào phần tử duy nhất của colorItems.
    */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "po_item_color_id", nullable = false)
    private PurchaseOrderColor purchaseOrderColor;
 
    @Column(name = "quantity_delivered", nullable = false)
    private Integer quantityDelivered; // Số lượng giao trong lần NÀY
}
