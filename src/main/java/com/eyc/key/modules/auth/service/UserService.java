package com.eyc.key.modules.auth.service;

import com.eyc.key.common.enums.UserStatus;
import com.eyc.key.modules.auth.dto.response.AuthResponse;
import com.eyc.key.modules.auth.entity.RefreshToken;
import com.eyc.key.modules.auth.entity.User;
import com.eyc.key.modules.auth.repository.RefreshTokenRepository;
import com.eyc.key.modules.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${security.max-failed-attempts}")
    int maxFailedAttempts;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Value("${security.lock-duration-minutes:5}")
    private int lockDurationMinutes;


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleFailedLogin(User user) {
        userRepository.incrementFailedAttempts(user.getUserId());
        if (user.getFailedLoginAttempts() + 1 >= maxFailedAttempts) {
            user.setLockedUntil(LocalDateTime.now().plusMinutes(lockDurationMinutes));
            user.setStatus(UserStatus.LOCKED);
            userRepository.save(user);
        }
    }

    public AuthResponse buildAuthResponse(User user, String deviceInfo, String ipAddress) {
        String accessToken = jwtService.generateAccessToken(user);
        String rawRefreshToken = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(rawRefreshToken)
                .expriesAt(LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000))
                .deviceInfo(deviceInfo)
                .ipAddress(ipAddress)
                .build();

        refreshTokenRepository.save(refreshToken);

        return AuthResponse.builder()
                .message("Đăng Nhập Thành Công")
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


    public User findUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        if (user.getStatus() == UserStatus.LOCKED && user.getLockedUntil() != null && LocalDateTime.now().isAfter(user.getLockedUntil())) {
            user.setStatus(UserStatus.ACTIVE);
            user.setLockedUntil(null);
            user.setFailedLoginAttempts(0);
            userRepository.save(user);

        }

        return user;
    }
}
