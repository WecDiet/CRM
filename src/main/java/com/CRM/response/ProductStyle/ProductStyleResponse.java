package com.CRM.response.ProductStyle;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductStyleResponse {
    private UUID id;
    private String name;
    private String code;
}
