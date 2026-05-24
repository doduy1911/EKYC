package com.eyc.key.modules.auth.dto.response;

import com.eyc.key.common.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AuthResponse {
    private String message;
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;
    private UserInfo user;


    @Data
    @Builder
    public static class UserInfo{
        private UUID userId;
        private String username;
        private String fullName;
        private Role role;
    }

}
