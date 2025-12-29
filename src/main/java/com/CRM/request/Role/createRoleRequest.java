package com.CRM.request.Role;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class createRoleRequest {
    private String name;
    private String description;
}
