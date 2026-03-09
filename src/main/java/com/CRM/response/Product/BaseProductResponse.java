package com.CRM.response.Product;

import java.util.UUID;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BaseProductResponse {
    private UUID id;
    private String code;
    private BaseProductDetail productDetail;
}
