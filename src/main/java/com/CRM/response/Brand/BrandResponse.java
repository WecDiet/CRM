package com.CRM.response.Brand;

import java.util.Date;
import java.util.UUID;

import com.CRM.response.Media.MediaResponse;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BrandResponse {
    private UUID id;
    private String name;
    private String description;
    private boolean highlighted;
    private MediaResponse image;
    private boolean inActive;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date createdDate;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date modifiedDate;
}
