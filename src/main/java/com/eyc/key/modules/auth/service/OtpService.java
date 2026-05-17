package com.eyc.key.modules.auth.service;

import com.eyc.key.common.enums.OtpType;
import com.eyc.key.modules.auth.entity.OtpVerification;
import com.eyc.key.modules.auth.entity.User;
import com.eyc.key.modules.auth.repository.OtpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${otp.expiration-minutes:5}")
    private int otpExpirationMinutes;



    @Transactional
    public void sendOtp(User user , OtpType otpType){
        otpRepository.deleteUnverifiedByUserAndType(user.getUserId(), otpType);

        String rawOtp = generateOtp();
        String hashedOtp = passwordEncoder.encode(rawOtp);

        OtpVerification otp = OtpVerification.builder()
                .user(user)
                .email(user.getEmail())
                .otpHash(hashedOtp)
                .otpType(otpType)
                .expiresAt(LocalDateTime.now().plusMinutes(otpExpirationMinutes))
                .createdAt(LocalDateTime.now())
                .build();

        otpRepository.save(otp);
        emailService.sendOtpEmail(user.getEmail(),user.getFull_name(), rawOtp , otpType);
        log.info("OTP sent to {} for type {}", user.getEmail(), otpType);

    }

    private String generateOtp(){
        int otp = 100000 + SECURE_RANDOM.nextInt(900000);
        return String.valueOf(otp);
    }
}
