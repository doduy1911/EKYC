package com.eyc.key.modules.kyc.service;

import com.eyc.key.modules.kyc.dto.Response.KycResponse;
import com.eyc.key.modules.kyc.dto.request.KycSubmitRequest;
import com.eyc.key.modules.kyc.entity.KycDocument;
import com.eyc.key.modules.kyc.entity.KycStateLog;
import com.eyc.key.modules.kyc.entity.KycSubmission;
import com.eyc.key.modules.kyc.enums.DocumentType;
import com.eyc.key.modules.kyc.enums.KycStatus;
import com.eyc.key.modules.kyc.enums.TriggeredByRole;
import com.eyc.key.modules.kyc.repository.KycDocumentRepository;
import com.eyc.key.modules.kyc.repository.KycStateLogRepository;
import com.eyc.key.modules.kyc.repository.KycSubmissionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j

public class KycService {
    private static final int MAX_SUBMISSION_COUNT = 3;
    private final KycSubmissionRepository kycSubmissionRepository;
    private final KycStateLogRepository kycStateLogRepository;
    private final KycFileService kycFileService;
    private final KycDocumentRepository kycDocumentRepository;
    @Transactional
    public KycResponse submitKyc(UUID userId,
                                 KycSubmitRequest request,
                                 MultipartFile frontImage,
                                 MultipartFile backImage,
                                 MultipartFile selfieImage) throws IOException {
        if (kycSubmissionRepository.hasActiveSubmission(userId)) {
            throw new RuntimeException("Bạn đã có hồ sơ KYC đang được xử lý");
        }
        kycSubmissionRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .ifPresent(prev -> {
                    if (prev.getSubmissionCount() >= MAX_SUBMISSION_COUNT) {
                        throw new RuntimeException("Bạn đã nộp tối đa " + MAX_SUBMISSION_COUNT + " lần");
                    }
                });

        KycSubmission kycSubmission = KycSubmission.builder()
                .userId(userId)
                .fullName(request.getFullName())
                .identityNumber(request.getIdentityNumber())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .nationality(request.getNationality())
                .placeOfOrigin(request.getPlaceOfOrigin())
                .placeOfOrigin(request.getPlaceOfOrigin())
                .idIssueDate(request.getIdIssueDate())
                .idExpiryDate(request.getIdExpiryDate())
                .status(KycStatus.DRAFT)
                .build();
        kycSubmissionRepository.save(kycSubmission);

        saveDocument(kycSubmission,frontImage, DocumentType.FRONT);
        saveDocument(kycSubmission, backImage, DocumentType.BACK);

        if (selfieImage != null && !selfieImage.isEmpty()){
            saveDocument(kycSubmission,selfieImage,DocumentType.SELFIE);
        }
        transition(kycSubmission, KycStatus.SUBMITTED , userId , TriggeredByRole.USER , null);

        kycSubmission.setSubmittedAt(LocalDateTime.now());
        kycSubmission.setSubmissionCount(kycSubmission.getSubmissionCount() + 1 );
        kycSubmissionRepository.save(kycSubmission);
        return toResponse(kycSubmission);
    }

    public KycResponse getMyKyc(UUID userId){
        KycSubmission submission = kycSubmissionRepository
                .findTopByUserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new RuntimeException("Bạn chưa nộp Hồ sơ "));

        return toResponse(submission);
    }

    private void saveDocument(KycSubmission submission,
                              MultipartFile file,
                              DocumentType type) throws IOException {
        KycDocument doc = kycFileService.saveFile(file,submission.getUserId(),type);
//        System.out.println("doc"+ doc);
        doc.setKycSubmission(submission);
        kycDocumentRepository.save(doc);
    }

    private void  transition(KycSubmission submission, KycStatus newStatus , UUID triggeredBy , TriggeredByRole role , String note) {
        KycStatus oldStatus = submission.getStatus();
        submission.transitionTo(newStatus);

        KycStateLog log = KycStateLog.builder()
                .kycSubmission(submission)
                .kycStatus(oldStatus)
                .toStatus(newStatus)
                .triggeredBy(triggeredBy)
                .triggeredByRole(role)
                .note(note)
                .build();

        kycStateLogRepository.save(log);
    }


    private KycResponse toResponse(KycSubmission s ){
        List<KycResponse.DocumentInfo> docs = kycDocumentRepository
                .findByKycSubmissionId(s.getId())
                .stream()
                .map(d -> KycResponse.DocumentInfo.builder()
                        .id(d.getId())
                        .documentType(d.getDocumentType().name())
                        .fileName(d.getFileName())
                        .uploadedAt(d.getUploadedAt())
                        .build()
                )
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
                .placeOfResidence(s.getPlaceOfOrigin())
                .idIssueDate(s.getIdIssueDate())
                .idExpiryDate(s.getIdExpiryDate())
                .status(s.getStatus())
                .previousStatus(s.getPreviousStatus())
                .rejectReason(s.getRejectReason())
                .rejectNote(s.getRejectNote())
                .submissionCount(s.getSubmissionCount())
                .submittedAt(s.getSubmittedAt())
                .build();
    }





}
