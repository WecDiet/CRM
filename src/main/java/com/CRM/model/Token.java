package com.CRM.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Token extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "refresh_token", length = 255) // refresh token khi token hết hạn sử dụng (refresh token)
    private String refreshToken;

    @Column(name = "token_type", length = 50)
    private String tokenType; // kiểu token: Bearer, JWT, v.v.

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate; // thời gian hết hạn của token

    @Column(name = "refresh_expiration_date")
    private LocalDateTime refreshExpirationDate; // thời gian hết hạn của refresh token

    @Column(name = "device_token")
    private String deviceToken;

    @Column(name = "device_name", length = 100)
    private String deviceName;
}
