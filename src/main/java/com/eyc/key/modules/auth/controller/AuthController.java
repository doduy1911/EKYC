package com.eyc.key.modules.auth.controller;

import com.eyc.key.modules.auth.dto.request.LoginRequest;
import com.eyc.key.modules.auth.dto.request.ResgisterRequest;
import com.eyc.key.modules.auth.dto.request.VerifyOtpRequest;
import com.eyc.key.modules.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody ResgisterRequest resgisterRequest){
        authService.register(resgisterRequest);
        return ResponseEntity.ok().build();

    }

    @PostMapping("/resend-otp/{username}")
    public ResponseEntity<?> resendOtp(@PathVariable String username) {
        authService.resendOtp(username);
        return ResponseEntity.ok(Map.of("message", "OTP mới đã được gửi đến email của bạn."));
    }

    @PostMapping("verify-otp/{username}")
    public ResponseEntity<?> verifyOtp(@PathVariable String username , @Valid @RequestBody VerifyOtpRequest otpRequest){
        authService.verifyRegistrationOtp(username, otpRequest);
        return ResponseEntity.ok(Map.of("message","Xác Thực Tài Khoản Thành Công , Bạn có thể đăng nhập"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest){
        authService.login(loginRequest);
        return ResponseEntity.ok(Map.of("message","Đăng Nhập Thành Công"));
    }

}
