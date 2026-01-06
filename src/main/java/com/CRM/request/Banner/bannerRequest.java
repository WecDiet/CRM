package com.CRM.request.Banner;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class bannerRequest {
    private String title;
    private boolean inActive;
    private int seq; // Số thứ tự của banner từ 1 -> 4
    private UUID brandID;
}
