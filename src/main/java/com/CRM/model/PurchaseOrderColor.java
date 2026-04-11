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
@Table(name = "po_item_colors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderColor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "po_item_id", nullable = false)
    private PurchaseOrderItem purchaseOrderItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pattern_color_id")
    private PatternColor patternColor;

    @Column(name = "quantity_ordered", nullable = false)
    private Integer quantityOrdered;

    @Column(name = "quantity_received", nullable = false)
    private Integer quantityReceived;

    /**
     * Tiện ích kiểm tra màu này đã giao đủ chưa.
    */
    public boolean isFullyReceived() {
        return quantityReceived != null
                && quantityOrdered != null
                && quantityReceived >= quantityOrdered;
    }
 
    /**
     * Số lượng còn cần giao cho màu này.
    */
    public int getRemainingQuantity() {
        int ordered = quantityOrdered != null ? quantityOrdered : 0;
        int received = quantityReceived != null ? quantityReceived : 0;
        return ordered - received;
    }
}
