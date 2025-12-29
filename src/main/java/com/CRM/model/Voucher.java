package com.CRM.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(name = "max_discount", nullable = false)
    private BigDecimal maxDiscount;

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

    @ManyToMany
    @JoinTable(name = "voucher_product", joinColumns = @JoinColumn(name = "voucher_id"), inverseJoinColumns = @JoinColumn(name = "product_id"))
    private Set<Product> products;

    @ManyToMany(mappedBy = "vouchers")
    private Set<Customer> customers;

}
