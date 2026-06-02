package com.eyc.key.modules.kyc.controller;

import com.eyc.key.modules.kyc.dto.Response.KycResponse;
import com.eyc.key.modules.kyc.dto.request.KycSubmitRequest;
import com.eyc.key.modules.kyc.service.KycService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/kyc")
@RequiredArgsConstructor
public class kycController {
    private final KycService kycService;

    @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<KycResponse> submit(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @Valid @ModelAttribute KycSubmitRequest request,
            @RequestPart("frontImage")MultipartFile frontImage ,
            @RequestPart("backImage") MultipartFile backImage,
            @RequestPart(value = "selfieImage", required = false) MultipartFile selfieImage)
            throws IOException{

        KycResponse response = kycService.submitKyc(
                userId, request, frontImage, backImage, selfieImage);
        return ResponseEntity.ok(response);
    }



}
