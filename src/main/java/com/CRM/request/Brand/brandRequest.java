package com.CRM.request.Brand;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandRequest {
    private String name;
    private String description;
    private String category;
    private boolean active;
    private boolean highlighted;
}
