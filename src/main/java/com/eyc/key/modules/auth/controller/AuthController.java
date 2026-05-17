package com.eyc.key.modules.auth.controller;

import com.eyc.key.modules.auth.dto.ResgisterRequest;
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

}
