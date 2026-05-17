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
    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;
    @Value("${security.max-failed-attempts:5}")
    private int maxFailedAttempts;
    @Value("${security.lock-duration-minutes:30}")
    private int lockDurationMinutes;

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
        User user = findUserByUsername(username);
        System.out.println(user);

        if (user.getStatus() != UserStatus.PENDING_VERIFICATION) {
            throw new RuntimeException("Tài khoản đã được xác thực");
        }

        otpService.sendOtp(user, OtpType.REGISTER);
    }

    @Transactional
    public void verifyRegistrationOtp(String  username, VerifyOtpRequest otp) {
        User user  = findUserByUsername(username);
        if (user.getStatus() != UserStatus.PENDING_VERIFICATION) {
            throw new RuntimeException("Tài Khoản đã đưpọc xác thực");
        }

        otpService.verifyOtp(user,otp.getOtp(),OtpType.REGISTER);

        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        log.info("User verified: {}", user.getUsername());
    }

    @Transactional
    public AuthResponse login(LoginRequest loginRequest){
        System.out.println(loginRequest);
        User user = findUserByUsername(loginRequest.getUsername());

        if (!user.isAccountNonLocked()) {
            throw new LockedException("Tài khoản của bạn bị khóa đến " + user.getLockedUntil());
        }

        if (user.getStatus() != UserStatus.PENDING_VERIFICATION) {
            throw new DisabledException("Tài khoản của bạn chưa được xác thực Email");
        }
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
        }catch (BadCredentialsException e){
            handleFailedLogin(user);
            throw new BadCredentialsException("Sai username hoắc mật khẩu");
        };
        userRepository.resetFailedAttempts(user.getUserId());
        return buildAuthResponse(user, loginRequest.getDeviceInfo(), null);
    }


    private AuthResponse buildAuthResponse(User user , String deviceInfo , String ipAddress){
        String accessToken = jwtService.generateAccessToken(user);
        String rawRefreshToken = UUID.randomUUID().toString();
        RefreshToken  refreshToken = RefreshToken.builder()
                .user(user)
                .token(rawRefreshToken)
                .expriesAt(LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000))
                .deviceInfo(deviceInfo)
                .ipAddress(ipAddress)
                .build();

        refreshTokenRepository.save(refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(rawRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpiration())
                .user(AuthResponse.UserInfo.builder()
                        .userId(user.getUserId())
                        .username(user.getUsername())
                        .fullName(user.getFull_name())
                        .role(user.getRole())
                        .build()
                )
                .build();
    }

    private void handleFailedLogin(User user){
        if (user.getFailedLoginAttempts() + 1 >= maxFailedAttempts){
            user.setLockedUntil(LocalDateTime.now().plusSeconds(lockDurationMinutes));
            user.setStatus(UserStatus.LOCKED);
            userRepository.save(user);
            log.warn("User loked: {}", user.getUsername());

        }
    }
    private User findUserByUsername(String username){
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
    }







}
