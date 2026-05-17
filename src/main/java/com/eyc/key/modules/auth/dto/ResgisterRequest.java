package com.eyc.key.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResgisterRequest {
    @NotBlank(message = "Username không được trống")
    @Size(min = 6, max = 50, message = "Username từ 6-50 ký tự")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username chỉ chứa chữ, số, dấu gạch dưới")
    private String username;

    @NotBlank(message = "Email không được trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Mật khẩu không được trống")
    @Size(min = 8, message = "Mật khẩu tối thiểu 8 ký tự")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*]).+$",
            message = "Mật khẩu phải có chữ hoa, số và ký tự đặc biệt")
    private String password;

    @NotBlank(message = "Họ tên không được trống")
    @Size(max = 100)
    private String fullName;

    @Pattern(regexp = "^(\\+84|0)[0-9]{9}$", message = "Số điện thoại không hợp lệ")
    private String phoneNumber;

    private String address;

}
