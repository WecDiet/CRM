package com.CRM.response.Product;

import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ProductResponse {
    private UUID id;
    private boolean status;
    private String skuCode;
    private boolean inActive;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date createdDate;
}
