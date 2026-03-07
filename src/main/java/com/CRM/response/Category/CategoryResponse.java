package com.CRM.response.Category;

import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CategoryResponse {
    private UUID id;
    private String name;
    private Boolean inActive;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date createdDate;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date modifiedDate;

}
