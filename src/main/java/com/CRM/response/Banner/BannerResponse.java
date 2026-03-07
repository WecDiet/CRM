package com.CRM.response.Banner;

import java.util.Date;
import java.util.UUID;

import com.CRM.response.Media.MediaResponse;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BannerResponse {
    private UUID id;
    private String name;
    private int seq;
    private boolean inActive;
    private MediaResponse image;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date createdDate;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date modifiedDate;
}
