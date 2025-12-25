package com.CRM.response.Product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailResponse {
    Long id;
    String name;
    String code;
    String description;
    Double price;
    boolean status;
    String slug;
}
