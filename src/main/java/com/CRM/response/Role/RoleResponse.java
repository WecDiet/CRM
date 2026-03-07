package com.CRM.response.Role;

import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RoleResponse {
    private UUID id;
    private String name;
    private String description;
    private Boolean inActive;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date createdDate;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date modifiedDate;
    private Long deletedAt;
    private boolean isDeleted;
}
