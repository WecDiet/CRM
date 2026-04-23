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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "defective_stocks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DefectiveStock extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Biết chính xác lô hàng nào, màu nào bị lỗi
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transfer_ticket_color_id", nullable = false)
    private TransferTicketColor transferTicketColor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "quantity_defective", nullable = false)
    private Integer quantityDefective;

    @Column(name = "defective_note", columnDefinition = "TEXT")
    private String defectiveNote; // Mô tả lỗi cụ thể

    // Dùng Image có sẵn — follow đúng pattern codebase
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
        name = "defective_stock_images",
        joinColumns = @JoinColumn(name = "defective_stock_id"),
        inverseJoinColumns = @JoinColumn(name = "image_id")
    )
    @Builder.Default
    private List<Image> images = new ArrayList<>();

    // PENDING_RETURN: chờ xuất trả kho
    // RETURNED: đã trả kho
    // DISPOSED: thanh lý
    @Column(name = "status", length = 50)
    private String status;
}
