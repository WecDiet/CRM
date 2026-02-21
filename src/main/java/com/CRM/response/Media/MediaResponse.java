package com.CRM.response.Media;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MediaResponse {
    private UUID id;
    private String imageUrl;
}
