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
@Table(name = "transfer_ticket_colors")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class TransferTicketColor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transfer_ticket_item_id", nullable = false)
    private TransferTicketItem transferTicketItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pattern_color_id")
    private PatternColor patternColor; // null = không phân biệt màu

    @Column(name = "quantity_sent", nullable = false)
    private Integer quantitySent;

    @Column(name = "quantity_received")
    private Integer quantityReceived;     // Số lượng cửa hàng thực tế nhận được
    
    public boolean isFullyReceived() {
        // Giao 1 lần → chỉ cần check status của TransferTicket
        // Field này dùng khi cần biết từng màu đã confirm chưa
        return transferTicketItem.getTransferTicket().getStatus().equals("COMPLETED");
    }
}
