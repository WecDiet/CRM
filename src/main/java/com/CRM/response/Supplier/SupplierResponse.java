package com.CRM.response.Supplier;

import java.util.UUID;

import com.CRM.response.Media.MediaResponse;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SupplierResponse {
    private UUID id;
    private String name;
    private Integer rating;
    private MediaResponse image;
    private boolean inActive;
}
