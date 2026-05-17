package com.eyc.key.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VerifyOtpRequest {
    @NotBlank(message = "Otp Không được để trống ")
    @Size(min = 6, max = 6 , message = "OTP phải có đủ 6 kí tự ")
    private String otp;
}
