package com.CRM.response.ProductStyle;

import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ProductStyleResponse {
    private UUID id;
    private String name;
    private String code;
}
