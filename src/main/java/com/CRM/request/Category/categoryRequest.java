package com.CRM.request.Category;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CategoryRequest {
    private String name;
    private boolean active;
}
