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

        kycSubmission.setSubmittedAt(LocalDate.now());
        kycSubmission.setSubmissionCount(kycSubmission.getSubmissionCount() + 1 );
        kycSubmissionRepository.save(kycSubmission);





        return null;
    }

    private void saveDocument(KycSubmission submission,
                              MultipartFile file,
                              DocumentType type) throws IOException {
        KycDocument doc = kycFileService.saveFile(file,submission.getUserId(),type);
        System.out.println("doc"+ doc);
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



}
