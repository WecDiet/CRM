package com.CRM.request.Role;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequest {
    private String name;
    private String description;
    private boolean active;
}
