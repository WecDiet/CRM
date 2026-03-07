package com.CRM.response.Supplier;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class SupplierDetailResponse {
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

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate colabDate;
}
