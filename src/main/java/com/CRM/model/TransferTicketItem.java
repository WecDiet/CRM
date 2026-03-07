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
@Table(name = "transfer_ticket_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferTicketItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transfer_ticket_id", nullable = false)
    private TransferTicket transferTicket;

    // Sản phẩm cụ thể được điều chuyển
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // 2. Số lượng thực xuất (Số lượng thực tế rời kho tổng)
    @Column(name = "quantity_sent")
    private Integer quantitySent;

    @Column(name = "quantity_received")
    private Integer quantityReceived;     // Số lượng cửa hàng thực tế nhận được
}
