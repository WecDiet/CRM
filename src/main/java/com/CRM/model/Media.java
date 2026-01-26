package com.CRM.model;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "media")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Media extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "image_url", length = 500) // Trường image_url là địa chỉ ảnh (thường là URL trên Cloudinary)
    @JsonProperty("image_url")
    private String imageUrl;

    @Column(name = "public_id", length = 500) // Trường public_id là ID công khai của ảnh trên Cloudinary
    private String publicId;

    // Trường alt_text là mô tả ảnh, thường dùng để cải thiện SEO và accessibility,
    // Trợ năng (screen reader)
    @Column(name = "alt_text", length = 500)
    private String altText;

    // ID của thực thể liên quan (User/Product/Story...)
    @Column(name = "reference_id", length = 100)
    private UUID referenceId;

    // "USER", "PRODUCT", "STORY", "CITY", "SLIDER", "STORE" , "BANNER",
    // "COLLABORATION", "WAREHOUSE"
    @Column(name = "reference_type", length = 50)
    private String referenceType;

    // THUMBNAIL: ảnh nhỏ (thumbnail) hiển thị trong danh sách.
    // GALLERY: ảnh chi tiết trong trang sản phẩm.
    // IMAGE: ảnh chung chung, có thể dùng cho nhiều mục đích khác nhau.
    // AVATAR: ảnh đại diện của người dùng (user profile).
    @Column(name = "type", length = 300)
    private String type;

}
