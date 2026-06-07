package com.eyc.key.modules.kyc.controller;

import com.eyc.key.modules.kyc.dto.Response.KycResponse;
import com.eyc.key.modules.kyc.dto.request.KycReviewRequest;
import com.eyc.key.modules.kyc.enums.KycStatus;
import com.eyc.key.modules.kyc.service.KycService;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/staff/kyc")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('STAFF','ADMIN')")
public class KycAdminController {
    private final KycService kycService;

    @GetMapping
    public ResponseEntity<Page<KycResponse>> getAll(
            @RequestParam(required = false) KycStatus status,
            @PageableDefault(size = 20 ,sort = "CreatedAt") Pageable pageable
    ){
        if (status != null ){
            return  ResponseEntity.ok(kycService.getSubmissionsByStatus(status,pageable));
        }
        return ResponseEntity.ok(kycService.getAllSubmissions(pageable));
    }

    // xem chi tiết 1 submission
    @GetMapping("/{userId}")
    public ResponseEntity<KycResponse> getById(@PathVariable UUID userId) {
        return ResponseEntity.ok(kycService.getSubmissionById(userId));
    }

    @PutMapping("/{userId}/start-review")
    public ResponseEntity<KycResponse> startReview(
            @PathVariable UUID userId,
            @AuthenticationPrincipal(expression = "userId") UUID reviewerId) {
        return ResponseEntity.ok(kycService.startReview(userId, reviewerId));
    }
    @PutMapping("/{submissionId}/review")
    public ResponseEntity<KycResponse> review(
            @PathVariable UUID submissionId,
            @AuthenticationPrincipal(expression = "userId") UUID reviewerId,
            @Validated @RequestBody KycReviewRequest request) {
        return ResponseEntity.ok(kycService.reviewKyc(submissionId, reviewerId, request));
    }


}
