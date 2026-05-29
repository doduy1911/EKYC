package com.eyc.key.modules.auth.controller;

import com.eyc.key.modules.auth.dto.request.LoginRequest;
import com.eyc.key.modules.auth.dto.request.ResgisterRequest;
import com.eyc.key.modules.auth.dto.request.VerifyOtpRequest;
import com.eyc.key.modules.auth.dto.response.OtpResponse;
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
    public ResponseEntity<OtpResponse> register(@Valid @RequestBody ResgisterRequest resgisterRequest){
        OtpResponse result =  authService.register(resgisterRequest);
        return ResponseEntity.ok(result);

    }

    @PostMapping("/resend-otp/{username}")
    public ResponseEntity<OtpResponse> resendOtp(@PathVariable String username) {
        OtpResponse result = authService.resendOtp(username);
        return ResponseEntity.ok(result);
    }



    @PostMapping("verify-otp/{username}")
    public ResponseEntity<OtpResponse> verifyOtp(
            @PathVariable String username,
            @Valid @RequestBody VerifyOtpRequest otpRequest) {

        OtpResponse result = authService.verifyRegistrationOtp(username, otpRequest);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(authService.login(loginRequest));
    }

}
