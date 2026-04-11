package com.CRM.request.Store;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreFilterRequest {
    
    private String name;

    private String street;

    private String ward;

    private String district;

    private String city;

    private String country;
}
