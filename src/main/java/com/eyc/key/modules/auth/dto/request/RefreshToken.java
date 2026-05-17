package com.eyc.key.modules.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshToken {
    @NotBlank(message = "Resfresh Không được để trống ")
    private String refreshToken;
}
