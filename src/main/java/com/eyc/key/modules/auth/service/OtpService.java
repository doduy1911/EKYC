package com.eyc.key.modules.auth.service;

import com.eyc.key.common.enums.OtpType;
import com.eyc.key.modules.auth.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {


    @Transactional
    public void sendOtp(User user , OtpType otpType){
        System.out.println("user"+user);
        System.out.println("otpType"+otpType);
    }
}
