package com.stepby.shop_backend.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {
    private String accessToken;
    private String userId;
    private String email;
    private String role;
}
