package com.ekyc.kyc.controller;

import com.ekyc.kyc.dto.request.KycReviewRequest;
import com.ekyc.kyc.dto.response.KycResponse;
import com.ekyc.kyc.enums.KycStatus;
import com.ekyc.kyc.service.KycService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/staff/kyc")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")   // Cả STAFF lẫn ADMIN đều vào được
public class KycAdminController {

    private final KycService kycService;

    // GET /api/staff/kyc — danh sách tất cả
    @GetMapping
    public ResponseEntity<Page<KycResponse>> getAll(
            @RequestParam(required = false) KycStatus status,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {

        if (status != null) {
            return ResponseEntity.ok(kycService.getSubmissionsByStatus(status, pageable));
        }
        return ResponseEntity.ok(kycService.getAllSubmissions(pageable));
    }

    // GET /api/staff/kyc/{id} — xem chi tiết 1 submission
    @GetMapping("/{id}")
    public ResponseEntity<KycResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(kycService.getSubmissionById(id));
    }

    // PUT /api/staff/kyc/{id}/start-review — nhận hồ sơ để review
    @PutMapping("/{id}/start-review")
    public ResponseEntity<KycResponse> startReview(
            @PathVariable UUID id,
            @AuthenticationPrincipal(expression = "id") UUID reviewerId) {
        return ResponseEntity.ok(kycService.startReview(id, reviewerId));
    }

    // PUT /api/staff/kyc/{id}/review — duyệt hoặc từ chối
    @PutMapping("/{id}/review")
    public ResponseEntity<KycResponse> review(
            @PathVariable UUID id,
            @AuthenticationPrincipal(expression = "id") UUID reviewerId,
            @Valid @RequestBody KycReviewRequest request) {
        return ResponseEntity.ok(kycService.reviewKyc(id, reviewerId, request));
    }
}
