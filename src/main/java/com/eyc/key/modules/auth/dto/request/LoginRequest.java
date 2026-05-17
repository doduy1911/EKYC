package com.eyc.key.modules.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Username không được trống")
    private String username;

    @NotBlank(message = "Mật khẩu không được trống")
    private String password;

    private String deviceInfo;
}
