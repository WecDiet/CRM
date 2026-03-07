package com.CRM.response.Product;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BaseProductResponse {
    private String code;
    private BaseProductDetail productDetail;
}
