package com.CRM.model;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "po_deliverys")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderDelivery {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private LocalDate actualDeliveryDate; // Ngày thực tế hàng về
    
    @Column(name = "delivery_note")
    private String deliveryNote; // Ghi chú về việc giao hàng

    @OneToOne
    @JoinColumn(name = "purchase_order_id", nullable = false, unique = true)
    private PurchaseOrder purchaseOrder;
}
