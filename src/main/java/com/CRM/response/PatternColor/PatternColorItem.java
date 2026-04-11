package com.CRM.response.PatternColor;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PatternColorItem {

    private UUID id;

    private String lensColor;
    
    private String lensColorName;

    private String frameColor;

    private String frameColorName;
}
