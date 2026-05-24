package com.eyc.key.modules.auth.service;

import com.eyc.key.common.enums.OtpType;
import com.eyc.key.common.enums.Role;
import com.eyc.key.common.enums.UserStatus;
import com.eyc.key.modules.auth.dto.request.LoginRequest;
import com.eyc.key.modules.auth.dto.request.ResgisterRequest;
import com.eyc.key.modules.auth.dto.request.VerifyOtpRequest;
import com.eyc.key.modules.auth.dto.response.AuthResponse;
import com.eyc.key.modules.auth.entity.RefreshToken;
import com.eyc.key.modules.auth.entity.User;
import com.eyc.key.modules.auth.repository.RefreshTokenRepository;
import com.eyc.key.modules.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final UserService  userService;


    @Transactional
    public void register(ResgisterRequest resgisterRequest){
        String username = resgisterRequest.getUsername();
        if (userRepository.findByUsername(username).isPresent()){
            throw new RuntimeException("Username đã tồn tại");
        }
        if (userRepository.findByEmail(resgisterRequest.getEmail()).isPresent()){
            throw  new RuntimeException("Email đã tồn tại");
        }

        User user = User.builder()
                .username(resgisterRequest.getUsername())
                .email(resgisterRequest.getEmail())
                .password(passwordEncoder.encode(resgisterRequest.getPassword()))
                .full_name(resgisterRequest.getFullName())
                .phone_number(resgisterRequest.getPhoneNumber())
                .address(resgisterRequest.getAddress())
                .role(Role.USER)
                .status(UserStatus.PENDING_VERIFICATION)
                .build();
        userRepository.save(user);

        otpService.sendOtp(user,OtpType.REGISTER);
        log.info("User registered: {}", user.getUsername());
    }

    @Transactional
    public void resendOtp(String username) {
        System.out.println(username);
        User user = userService.findUserByUsername(username);
        System.out.println(user);

        if (user.getStatus() != UserStatus.PENDING_VERIFICATION) {
            throw new RuntimeException("Tài khoản đã được xác thực");
        }

        otpService.sendOtp(user, OtpType.REGISTER);
    }

    @Transactional
    public void verifyRegistrationOtp(String  username, VerifyOtpRequest otp) {
        User user  = userService.findUserByUsername(username);
        if (user.getStatus() != UserStatus.PENDING_VERIFICATION) {
            throw new RuntimeException("Tài Khoản đã đưpọc xác thực");
        }

        otpService.verifyOtp(user,otp.getOtp(),OtpType.REGISTER);

        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        log.info("User verified: {}", user.getUsername());
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userService.findUserByUsername(request.getUsername());

        if (!user.isAccountNonLocked()) {
            throw new LockedException("Tài khoản bị khóa đến " + user.getLockedUntil());
        }

        if (user.getStatus() == UserStatus.PENDING_VERIFICATION) {
            throw new DisabledException("Tài khoản chưa được xác thực email");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword()));
        } catch (BadCredentialsException e) {
            userService.handleFailedLogin(user);
            throw new BadCredentialsException("Sai username hoặc mật khẩu");
        }

        userRepository.resetFailedAttempts(user.getUserId());
        return userService.buildAuthResponse(user, request.getDeviceInfo(), null);
    }
}
