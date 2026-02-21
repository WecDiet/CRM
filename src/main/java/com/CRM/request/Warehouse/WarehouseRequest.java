package com.CRM.request.Warehouse;

import java.util.List;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseRequest {
    private String name;

    private String street;

    private String ward;

    private String district;

    private String city;

    private String country;

    private List<String> idsImageDelete;

}
