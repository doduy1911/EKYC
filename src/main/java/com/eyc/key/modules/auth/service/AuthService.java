package com.eyc.key.modules.auth.service;

import com.eyc.key.common.enums.OtpType;
import com.eyc.key.common.enums.Role;
import com.eyc.key.common.enums.UserStatus;
import com.eyc.key.modules.auth.dto.ResgisterRequest;
import com.eyc.key.modules.auth.entity.User;
import com.eyc.key.modules.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;

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
//        userRepository.save(user);

        otpService.sendOtp(user,OtpType.REGISTER);



    }



}
