package com.CRM.response.Warehouse;

import java.util.List;
import java.util.UUID;

import com.CRM.response.Media.MediaResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseResponse {
    private UUID id;
    
    private String name;

    private String street;

    private String ward;

    private String district;

    private String city;

    private String country;

    private boolean inActive;

    private List<MediaResponse> images;
}
