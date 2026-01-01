package com.CRM.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_email", columnList = "email"),
        @Index(name = "idx_user_username", columnList = "username"),
        @Index(name = "idx_user_phone", columnList = "phoneNumber"),
        @Index(name = "idx_user_slug", columnList = "slug"),
        @Index(name = "idx_user_role", columnList = "role_id"),
        @Index(name = "idx_user_status_email", columnList = "status,email"),
        @Index(name = "idx_user_status_username", columnList = "status,username"),
        @Index(name = "idx_users_status", columnList = "status"),
        @Index(name = "idx_users_slug", columnList = "slug", unique = true),
        @Index(name = "idx_users_lastname_middlename_firstname", columnList = "last_name, middle_name, first_name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", length = 45, nullable = false)
    private String firstName;

    @Column(name = "middle_name", length = 45, nullable = false)
    private String middleName;

    @Column(name = "last_name", length = 45, nullable = false)
    private String lastName;

    @Column(name = "slug", length = 150, nullable = false)
    private String slug;

    @Column(name = "email", length = 200, nullable = false, unique = true)
    private String email;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "phoneNumber", length = 45, unique = true)
    private String phoneNumber;

    @Column(name = "status")
    private boolean status;

    @Column(name = "userType")
    private int userType; // 1: Google, 2: Facebook, 3: email

    @Column(name = "username", length = 45, unique = true)
    private String username;

    @Column(name = "password", length = 64, nullable = false)
    private String password;

    @Override
    public String getUsername() {
        if (this.role != null && "role_customer".equalsIgnoreCase(this.role.getName())) {
            return this.email;
        }
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    // Lấy danh sách quyền của user
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority("role_" + role.getName()));
        return authorityList;
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Address> addresses = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Token> tokens;
}
