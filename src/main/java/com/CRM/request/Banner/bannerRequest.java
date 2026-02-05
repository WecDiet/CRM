package com.CRM.request.Banner;

import java.util.UUID;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BannerRequest {
    private String name;
    private int seq; // Số thứ tự của banner từ 1 -> 4
    private String brand;
    private boolean active;
}
