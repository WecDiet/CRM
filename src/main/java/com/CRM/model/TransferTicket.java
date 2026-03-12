package com.CRM.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "transfer_tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferTicket extends BaseEntity {
    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "transfer_code", length = 200)
    private String ticketCode;

    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse; // Kho Tổng

    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;   // Cửa hàng chỉ định

    @OneToMany(mappedBy = "transferTicket", cascade = CascadeType.ALL)
    private List<TransferTicketItem> items;
    
    @Column(name = "status")
    private String status; // PENDING, IN_TRANSIT, COMPLETED

    @Column(name = "expected_delivery_date")
    private LocalDate expectedDeliveryDate; // Ngày dự kiến hàng tới cửa hàng

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt; // Thời điểm cửa hàng xác nhận nhận hàng

    @Column(name = "note", columnDefinition = "TEXT")
    private String note; // Ghi chú tổng cho cả phiếu
}
