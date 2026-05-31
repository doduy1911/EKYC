package com.ekyc.kyc.service;

import com.ekyc.kyc.dto.request.KycReviewRequest;
import com.ekyc.kyc.dto.request.KycSubmitRequest;
import com.ekyc.kyc.dto.response.KycResponse;
import com.ekyc.kyc.entity.KycDocument;
import com.ekyc.kyc.entity.KycStateLog;
import com.ekyc.kyc.entity.KycSubmission;
import com.ekyc.kyc.enums.KycRejectReason;
import com.ekyc.kyc.enums.KycStatus;
import com.ekyc.kyc.repository.KycDocumentRepository;
import com.ekyc.kyc.repository.KycStateLogRepository;
import com.ekyc.kyc.repository.KycSubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KycService {

    private final KycSubmissionRepository submissionRepository;
    private final KycDocumentRepository documentRepository;
    private final KycStateLogRepository stateLogRepository;
    private final KycFileService fileService;
    private final KycEmailService emailService;

    private static final int MAX_SUBMISSION_COUNT = 3;

    // ==================== USER ACTIONS ====================

    @Transactional
    public KycResponse submitKyc(UUID userId,
                                 KycSubmitRequest request,
                                 MultipartFile frontImage,
                                 MultipartFile backImage,
                                 MultipartFile selfieImage) throws IOException {

        // Kiểm tra đã có submission active chưa
        if (submissionRepository.hasActiveSubmission(userId)) {
            throw new RuntimeException("Bạn đã có hồ sơ KYC đang được xử lý");
        }

        // Kiểm tra số lần nộp
        submissionRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .ifPresent(prev -> {
                    if (prev.getSubmissionCount() >= MAX_SUBMISSION_COUNT) {
                        throw new RuntimeException("Bạn đã nộp tối đa " + MAX_SUBMISSION_COUNT + " lần");
                    }
                });

        // Tạo submission mới
        KycSubmission submission = KycSubmission.builder()
                .userId(userId)
                .fullName(request.getFullName())
                .identityNumber(request.getIdentityNumber())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .nationality(request.getNationality())
                .placeOfOrigin(request.getPlaceOfOrigin())
                .placeOfResidence(request.getPlaceOfResidence())
                .idIssueDate(request.getIdIssueDate())
                .idExpiryDate(request.getIdExpiryDate())
                .status(KycStatus.DRAFT)
                .build();

        submissionRepository.save(submission);

        // Upload ảnh
        saveDocument(submission, frontImage, KycDocument.DocumentType.FRONT);
        saveDocument(submission, backImage, KycDocument.DocumentType.BACK);
        if (selfieImage != null && !selfieImage.isEmpty()) {
            saveDocument(submission, selfieImage, KycDocument.DocumentType.SELFIE);
        }

        // Transition DRAFT → SUBMITTED
        transition(submission, KycStatus.SUBMITTED, userId,
                KycStateLog.TriggeredByRole.USER, null);

        submission.setSubmittedAt(LocalDateTime.now());
        submission.setSubmissionCount(submission.getSubmissionCount() + 1);
        submissionRepository.save(submission);

        log.info("KYC submitted by user: {}", userId);
        return toResponse(submission);
    }

    public KycResponse getMyKyc(UUID userId) {
        KycSubmission submission = submissionRepository
                .findTopByUserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new RuntimeException("Bạn chưa nộp hồ sơ KYC"));
        return toResponse(submission);
    }

    // ==================== ADMIN/STAFF ACTIONS ====================

    @Transactional
    public KycResponse startReview(UUID submissionId, UUID reviewerId) {
        KycSubmission submission = findSubmission(submissionId);

        transition(submission, KycStatus.UNDER_REVIEW, reviewerId,
                KycStateLog.TriggeredByRole.STAFF, "Bắt đầu review");

        submission.setReviewedBy(reviewerId);
        submissionRepository.save(submission);

        return toResponse(submission);
    }

    @Transactional
    public KycResponse reviewKyc(UUID submissionId,
                                 UUID reviewerId,
                                 KycReviewRequest request) {
        KycSubmission submission = findSubmission(submissionId);

        KycStatus decision = request.getDecision();

        // Validate decision
        if (decision != KycStatus.APPROVED
                && decision != KycStatus.REJECTED
                && decision != KycStatus.RESUBMIT_REQUIRED) {
            throw new RuntimeException("Decision không hợp lệ");
        }

        // Bắt buộc có lý do khi reject
        if (decision == KycStatus.REJECTED && request.getRejectReason() == null) {
            throw new RuntimeException("Phải có lý do từ chối");
        }

        transition(submission, decision, reviewerId,
                KycStateLog.TriggeredByRole.ADMIN, request.getNote());

        submission.setReviewedBy(reviewerId);
        submission.setReviewedAt(LocalDateTime.now());
        submission.setRejectReason(request.getRejectReason());
        submission.setRejectNote(request.getNote());
        submissionRepository.save(submission);

        // Gửi email thông báo cho user
        emailService.sendReviewResultEmail(submission);

        log.info("KYC {} by reviewer: {}", decision, reviewerId);
        return toResponse(submission);
    }

    public Page<KycResponse> getSubmissionsByStatus(KycStatus status, Pageable pageable) {
        return submissionRepository.findByStatus(status, pageable)
                .map(this::toResponse);
    }

    public Page<KycResponse> getAllSubmissions(Pageable pageable) {
        return submissionRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::toResponse);
    }

    public KycResponse getSubmissionById(UUID submissionId) {
        return toResponse(findSubmission(submissionId));
    }

    // ==================== HELPERS ====================

    private void transition(KycSubmission submission,
                            KycStatus newStatus,
                            UUID triggeredBy,
                            KycStateLog.TriggeredByRole role,
                            String note) {
        KycStatus oldStatus = submission.getStatus();
        submission.transitionTo(newStatus);     // Validate trong entity

        // Ghi state log
        KycStateLog log = KycStateLog.builder()
                .kycSubmission(submission)
                .fromStatus(oldStatus)
                .toStatus(newStatus)
                .triggeredBy(triggeredBy)
                .triggeredByRole(role)
                .note(note)
                .build();

        stateLogRepository.save(log);
    }

    private void saveDocument(KycSubmission submission,
                              MultipartFile file,
                              KycDocument.DocumentType type) throws IOException {
        KycDocument doc = fileService.saveFile(file, submission.getId(), type);
        doc.setKycSubmission(submission);
        documentRepository.save(doc);
    }

    private KycSubmission findSubmission(UUID id) {
        return submissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ KYC"));
    }

    private KycResponse toResponse(KycSubmission s) {
        List<KycResponse.DocumentInfo> docs = documentRepository
                .findByKycSubmissionId(s.getId())
                .stream()
                .map(d -> KycResponse.DocumentInfo.builder()
                        .id(d.getId())
                        .documentType(d.getDocumentType().name())
                        .fileName(d.getFileName())
                        .fileSize(d.getFileSize())
                        .uploadedAt(d.getUploadedAt())
                        .build())
                .toList();

        return KycResponse.builder()
                .id(s.getId())
                .userId(s.getUserId())
                .fullName(s.getFullName())
                .identityNumber(s.getIdentityNumber())
                .dateOfBirth(s.getDateOfBirth())
                .gender(s.getGender())
                .nationality(s.getNationality())
                .placeOfOrigin(s.getPlaceOfOrigin())
                .placeOfResidence(s.getPlaceOfResidence())
                .idIssueDate(s.getIdIssueDate())
                .idExpiryDate(s.getIdExpiryDate())
                .status(s.getStatus())
                .previousStatus(s.getPreviousStatus())
                .rejectReason(s.getRejectReason())
                .rejectNote(s.getRejectNote())
                .submissionCount(s.getSubmissionCount())
                .submittedAt(s.getSubmittedAt())
                .reviewedAt(s.getReviewedAt())
                .documents(docs)
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .build();
    }
}
