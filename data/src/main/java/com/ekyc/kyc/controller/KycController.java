package com.ekyc.kyc.controller;

import com.ekyc.kyc.dto.request.KycSubmitRequest;
import com.ekyc.kyc.dto.response.KycResponse;
import com.ekyc.kyc.service.KycService;
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
public class KycController {

    private final KycService kycService;

    // POST /api/kyc/submit — nộp hồ sơ KYC
    // Dùng multipart/form-data vì có file upload
    @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<KycResponse> submit(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @Valid @ModelAttribute KycSubmitRequest request,
            @RequestPart("frontImage") MultipartFile frontImage,
            @RequestPart("backImage") MultipartFile backImage,
            @RequestPart(value = "selfieImage", required = false) MultipartFile selfieImage)
            throws IOException {

        KycResponse response = kycService.submitKyc(
                userId, request, frontImage, backImage, selfieImage);
        return ResponseEntity.ok(response);
    }

    // GET /api/kyc/me — xem trạng thái KYC của mình
    @GetMapping("/me")
    public ResponseEntity<KycResponse> getMyKyc(
            @AuthenticationPrincipal(expression = "id") UUID userId) {
        return ResponseEntity.ok(kycService.getMyKyc(userId));
    }
}
