package com.CRM.response.Product;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailResponse {
    private UUID id;
    private String name;
    private String code;
    private String description;
    private Double price;
    private boolean status;
    private String slug;
}
