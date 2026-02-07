package com.CRM.response.Product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasicProductResponse {
    private String name;
    private String code;
    private Double price;
}
