package com.CRM.response.Brand;

import java.util.UUID;

import com.CRM.response.Media.MediaResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandResponse {
    private UUID id;
    private String name;
    private String titleBrand;
    private boolean highlighted;
    private String slug;
    private MediaResponse image;
}
