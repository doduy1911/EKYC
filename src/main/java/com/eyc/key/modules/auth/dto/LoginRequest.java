package com.eyc.key.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Username không được để trống ")
    @Size(min = 8)
    private String Username;

    @NotBlank(message = "Mật Khẩu không được để trống ")
    @Size(min = 8)
    private String Password;
}
