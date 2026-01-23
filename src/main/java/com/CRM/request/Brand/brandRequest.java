package com.CRM.request.Brand;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BrandRequest {
    private String name;
    private String description;
    private String category;
    private boolean active;
    private boolean highlighted;
}
