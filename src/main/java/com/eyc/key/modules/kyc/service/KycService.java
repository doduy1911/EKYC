package com.eyc.key.modules.kyc.service;

import com.eyc.key.modules.kyc.dto.Response.KycResponse;
import com.eyc.key.modules.kyc.dto.request.KycSubmitRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KycService {
    public KycResponse submitKyc(UUID userId,
                                 KycSubmitRequest request,
                                 MultipartFile frontImage,
                                 MultipartFile backImage,
                                 MultipartFile selfieImage) throws IOException {

        return null;
    }
}
