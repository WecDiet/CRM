package com.CRM.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pattern_color")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatternColor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "lens_color")
    private String lensColor; // #000000

    @Column(name = "lens_color_name")
    private String lensColorName;

    @Column(name = "frame_color")
    private String frameColor; // #FF0000 (Nếu null thì là màu đơn)

    @Column(name = "frame_color_name")
    private String frameColorName;
}
