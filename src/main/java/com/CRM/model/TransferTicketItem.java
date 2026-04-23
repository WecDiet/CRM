package com.CRM.model;

import java.util.ArrayList;
import java.util.List;
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
@Table(name = "transfer_ticket_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferTicketItem{
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

    // // 2. Số lượng thực xuất (Số lượng thực tế rời kho tổng)
    // @Column(name = "quantity_sent")
    // private Integer quantitySent;

    // @Column(name = "quantity_received")
    // private Integer quantityReceived;     // Số lượng cửa hàng thực tế nhận được

    @OneToMany(mappedBy = "transferTicketItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TransferTicketColor> colorDetails = new ArrayList<>();

    @Transient
    public int getQuantitySent() {
        return colorDetails.stream()
            .mapToInt(c -> c.getQuantitySent() != null ? c.getQuantitySent() : 0)
            .sum();
    }

    @Transient
    public boolean isFullyReceived() {
        return !colorDetails.isEmpty() 
            && colorDetails.stream().allMatch(TransferTicketColor::isFullyReceived);
    }
}
