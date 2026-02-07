package com.CRM.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "vouchers")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Voucher extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "code", length = 100, nullable = false)
    private String code;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "discount_type", nullable = false)
    private String discountType; // "percentage" or "fixed"

    @Column(name = "discount", nullable = false)
    private BigDecimal discount;

    // Nếu voucher bị xóa vào thùng rác và hoặc đã xóa vĩnh viễn thì mặc dù customer có mã cũng không dùng được
    @Column(name = "status", nullable = false)
    private boolean status;

    /*
     * true = áp dụng cho tất cả sản phẩm,
     * false = chỉ áp dụng cho sản phẩm được chỉ định
     */
    @Column(name = "is_global", nullable = false)
    private boolean isGlobal;

    @Column(name = "start_date", nullable = false)
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime startDate;

    @Column(name = "expiration_date", nullable = false)
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime expirationDate;

    // 1. Tổng số lượng voucher phát hành (Ví dụ: chỉ có 100 mã)
    @Column(name = "quantity", nullable = false)
    private Integer quantity; 

    // 2. Số lượng đã sử dụng thực tế (Tăng lên mỗi khi có người dùng thành công)
    @Column(name = "used_count", nullable = false)
    private Integer usedCount = 0; 

    // 3. Giới hạn số lần dùng cho mỗi User (Ví dụ: 1 lần/người)
    @Column(name = "limit_per_user", nullable = false)
    private Integer limitPerUser; 

    // 4. Giá trị đơn hàng tối thiểu để được áp dụng (Ví dụ: đơn > 200k mới được dùng)
    @Column(name = "min_order_value", nullable = false)
    private BigDecimal minOrderValue;

    // --- XỬ LÝ ĐỒNG THỜI (CONCURRENCY) ---
    // Để tránh việc 2 người cùng tranh nhau mã cuối cùng
    @Version 
    private Long version;

    @ManyToMany
    @JoinTable(name = "voucher_product", joinColumns = @JoinColumn(name = "voucher_id"), inverseJoinColumns = @JoinColumn(name = "product_id"))
    private Set<Product> products;

    @ManyToMany(mappedBy = "vouchers")
    private Set<Customer> customers;

    // Một Voucher có thể được ghi nhận trong nhiều lần sử dụng (VoucherUsage)
    @OneToMany(mappedBy = "voucher")
    private Set<VoucherUsage> usages;

}
