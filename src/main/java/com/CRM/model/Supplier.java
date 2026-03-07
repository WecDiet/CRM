package com.CRM.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "suppliers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supplier extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "supplier_code", nullable = false, length = 50)
    private String supplierCode; // VD: NCC_RAYBAN

    @Column(name = "tax_code", length = 50)
    private String taxCode;

    @Column(name = "contact_person", length = 255)
    private String contactPerson;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "street", length = 255)
    private String street;

    @Column(name = "ward", length = 150)
    private String ward;

    @Column(name = "district", length = 150)
    private String district;

    @Column(name = "city", length = 150)
    private String city;

    @Column(name = "country", length = 150)
    private String country;

    @Column(name = "rating")
    private Integer rating; // đánh giá NCC

    @Column(columnDefinition = "TEXT")
    private String note;  // Ghi chú về thế mạnh hoặc chính sách chiết khấu

    @Column(name = "colab_date")
    private LocalDate colabDate;

    @OneToMany(mappedBy = "supplier", fetch = FetchType.LAZY)
    private Set<PurchaseOrder> purchaseOrders = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "image_id")
    private Image image;
}
