package com.stepby.shop_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;


@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = "email")})
@Getter
@Setter
@NoArgsConstructor
@ToString
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @Column(nullable = false, length = 50)
    private String realName;

    @Column(nullable = false)
    private LocalDate birthDate; // 생년월일

    @Enumerated(EnumType.STRING) // 성별 Enum
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false, length = 20)
    private String phoneNumber;

    @Column(nullable = false)
    private String zonecode; // 우편번호

    @Column(nullable = false, length = 255)
    private String address; // 기본 주소

    @Column(nullable = false, length = 255)
    private String detailAddress; // 상세 주소

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // ==== UserDetails 인터페이스 구성 ==== //

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { // 계정 만료 여부(true시 만료되지 않음)
        return true;
    }

    @Override
    public boolean isAccountNonLocked() { // 계정 잠금 여부(true시 만료되지 않음)
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() { // 비밀번호 만료 여부 (true시 만료되지 않음)
        return true;
    }

    @Override
    public boolean isEnabled() { // 계정 활성화 여부 (true시 활성화)
        return true;
    }

}
