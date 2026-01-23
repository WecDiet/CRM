package com.CRM.model;

import java.math.BigDecimal;
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
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_detail", indexes = {
        @Index(name = "idx_product_name", columnList = "name"),
        @Index(name = "idx_product_material", columnList = "material"),
        @Index(name = "idx_product_shape", columnList = "shape")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDetail extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "name", length = 500, nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price")
    private BigDecimal price;
    // Khung
    @Column(name = "frame", length = 100)
    private String frame;

    @Column(name = "frame_font", length = 100)
    private double frameFont;

    // Loại lens
    @Column(name = "lens", length = 100)
    private String lens;

    // Hình dáng
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

    @Column(name = "temple_length")
    private double temple_length;

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
    private List<Media> images = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pattern_color_id", nullable = false)
    private List<PatternColor> colors = new ArrayList<>();
}
