package com.CRM.response.Product;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class ProductDetailResponse {
    private UUID id;
    private String name;
    private String code;
    private String description;
    private Double price;
    private boolean status;
}
