package com.CRM.request.Brand;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class brandRequest {
    private String name;
    private String titleBrand;
    private boolean highlighted;
    private UUID category;
}
