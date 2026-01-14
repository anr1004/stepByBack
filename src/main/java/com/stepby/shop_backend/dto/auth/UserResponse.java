package com.stepby.shop_backend.dto.auth;

import com.stepby.shop_backend.entity.Gender;
import com.stepby.shop_backend.entity.Role;
import com.stepby.shop_backend.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponse {
    private String id;
    private String email;
    private Role role;
    private String realName;
    private LocalDate birthDate;
    private Gender gender;
    private String phoneNumber;
    private String zonecode;
    private String address;
    private String detailAddress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public static UserResponse fromEntity(User user){
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .realName(user.getRealName())
                .birthDate(user.getBirthDate())
                .gender(user.getGender())
                .phoneNumber(user.getPhoneNumber())
                .zonecode(user.getZonecode())
                .address(user.getAddress())
                .detailAddress(user.getDetailAddress())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
