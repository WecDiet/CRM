package com.CRM.request.Product;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ProductFilter {
    private String name; // tìm theo tên sản phẩm (like)

    private BigDecimal minPrice; // giá tối thiểu

    private BigDecimal maxPrice; // giá tối đa

    private String category; // danh mục

    private String brand; // thương hiệu

    private Boolean inStock; // chỉ lấy hàng còn tồn kho

    private Integer minRating; // đánh giá tối thiểu (nếu có)
}
