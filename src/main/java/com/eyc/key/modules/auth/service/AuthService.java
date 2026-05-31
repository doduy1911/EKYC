package com.eyc.key.modules.auth.service;

import com.eyc.key.common.audit.AuditAction;
import com.eyc.key.common.audit.AuditLogsService;
import com.eyc.key.common.enums.OtpType;
import com.eyc.key.common.enums.Role;
import com.eyc.key.common.enums.UserStatus;
import com.eyc.key.common.exception.OtpVerifyException;
import com.eyc.key.modules.auth.dto.request.LoginRequest;
import com.eyc.key.modules.auth.dto.request.ResgisterRequest;
import com.eyc.key.modules.auth.dto.request.VerifyOtpRequest;
import com.eyc.key.modules.auth.dto.response.AuthResponse;
import com.eyc.key.modules.auth.dto.response.OtpResponse;
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
    private final AuditLogsService  auditLogsService;


    @Transactional
    public OtpResponse register(ResgisterRequest resgisterRequest){
        String username = resgisterRequest.getUsername();
        if (userRepository.findByUsername(username).isPresent()){
            auditLogsService.log(
                    username,
                    AuditAction.REGISTER,
                    false,
                    "Username đã tồn tại "
            );
            throw new RuntimeException("Username đã tồn tại");

        }
        if (userRepository.findByEmail(resgisterRequest.getEmail()).isPresent()){
            auditLogsService.log(
                    username,
                    AuditAction.REGISTER,
                    true,
                    "Email đã tồn tại "
            );
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
        auditLogsService.log(
                user.getUserId(),
                username,
                AuditAction.REGISTER,
                false,
                "Đã gửi OTP cho User"
        );
        return OtpResponse.success("Otp đã được gửi đi ");
    }

    @Transactional
    public OtpResponse resendOtp(String username) {
        User user = userService.findUserByUsername(username);

        if (user.getStatus() != UserStatus.PENDING_VERIFICATION) {
            throw OtpVerifyException.business("Tài khoản đã được xác thực",0);
        }

        otpService.sendOtp(user, OtpType.REGISTER);
        return OtpResponse.success("Vui Lòng Kiểm tra email của bạn ");
    }

    @Transactional(noRollbackFor = OtpVerifyException.class)
    public OtpResponse verifyRegistrationOtp(String username, VerifyOtpRequest otp) {

        User user = userService.findUserByUsername(username);

        if (user.getStatus() != UserStatus.PENDING_VERIFICATION) {
            throw OtpVerifyException.business("Tài khoản đã được xác thực", 0);
        }

        OtpResponse result = otpService.verifyOtp(user, otp.getOtp(), OtpType.REGISTER);

        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        log.info("User verified: {}", user.getUsername());

        return result;
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
