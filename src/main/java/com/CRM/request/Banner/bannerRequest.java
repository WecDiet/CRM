package com.CRM.request.Banner;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BannerRequest {
    private String name;
    private int seq; // Số thứ tự của banner từ 1 -> 4
    private String brand;
    private boolean active;
}
