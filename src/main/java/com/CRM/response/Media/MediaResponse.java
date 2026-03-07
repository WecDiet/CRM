package com.CRM.response.Media;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class MediaResponse {
    private UUID id;
    private String imageUrl;
}
