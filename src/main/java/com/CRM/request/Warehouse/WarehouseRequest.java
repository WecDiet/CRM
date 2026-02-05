package com.CRM.request.Warehouse;

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

}
