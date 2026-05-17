package com.eyc.key.modules.auth.controller;

import com.eyc.key.modules.auth.dto.ResgisterRequest;
import com.eyc.key.modules.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
