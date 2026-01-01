package com.CRM.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_detail")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDetail extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Khung
    @Column(name = "frame", length = 100)
    private String frame;

    // Loại lens
    @Column(name = "lens", length = 100)
    private String lens;

    // Hình dàng
    @Column(name = "shape", length = 100)
    private String shape;

    // Chất liệu
    @Column(name = "material", length = 100)
    private String material;

    // Chiều rộng của lens
    @Column(name = "lens_width")
    private double lens_Width;

    // Chiều cao của lens
    @Column(name = "lens_height")
    private double lens_Height;

    // Vòng cầu của kính
    @Column(name = "bridge")
    private double bridge;

    // Nước sản xuất
    @Column(name = "country", length = 100)
    private String country;

    // Nhà sản xuất
    @Column(name = "manufacturer", length = 200)
    private String manufacturer;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "product_image", joinColumns = @JoinColumn(name = "product_detail_id"), inverseJoinColumns = @JoinColumn(name = "media_id"))
    private List<Media> image = new ArrayList<>();
}
