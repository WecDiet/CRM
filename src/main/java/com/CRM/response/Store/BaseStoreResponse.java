package com.CRM.response.Store;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseStoreResponse {
    private UUID id; 

    private String name;

    private String country;

    private String city;

    private boolean inActive;

}
