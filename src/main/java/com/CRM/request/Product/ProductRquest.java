package com.CRM.request.Product;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRquest {

    // Product Style

    private String styleName;
    
    private String code;

    private String brandId;

    // Product

    private String name;

    private boolean status;

    private String description;

    private BigDecimal price;

    private String frame;

    private double frameFont;

    private String material;

    private double lens_Width;

    private double lens_Height;

    private double temple_length;

    private double bridge;

    private String country;

    private String manufacturer;

    // Product color 

    private String lensColor;

    private String lensColorName;

    private String frameColor;

    private String frameColorName;

    // Inventory 

    private String warehouseId;

    private Integer quantity;

    private String type; // Nhập kho, Xuất kho, Điều chuyển

    private String referenceCode; // Mã tham chiếu liên quan đến giao dịch kho hàng (nếu có)
    
    private String note;
}
