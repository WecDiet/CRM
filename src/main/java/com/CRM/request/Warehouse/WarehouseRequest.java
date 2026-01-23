package com.CRM.request.Warehouse;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class WarehouseRequest {
    private String name;

    private String street;

    private String ward;

    private String district;

    private String city;

    private String country;

    private boolean active;

}
