package com.CRM.request.Role;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class updateRoleRequest {
    private String name;
    private String description;
    private Boolean isActive;
}
