package com.CRM.request.Supplier;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class SupplierRequest {
    private String name;
    private String taxCode;
    private String contactPerson;
    private String phone;
    private String email;
    private String street;
    private String ward;
    private String district;
    private String city;
    private String country;
    private Integer rating;
    private String note;

    private Integer startDay;
    private Integer startMonth;
    private Integer startYear;
}
