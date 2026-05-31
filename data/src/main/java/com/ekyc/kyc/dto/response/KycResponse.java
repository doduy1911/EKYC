package com.ekyc.kyc.dto.response;

import com.ekyc.kyc.enums.KycRejectReason;
import com.ekyc.kyc.enums.KycStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class KycResponse {
    private UUID id;
    private UUID userId;
    private String fullName;
    private String identityNumber;
    private LocalDate dateOfBirth;
    private String gender;
    private String nationality;
    private String placeOfOrigin;
    private String placeOfResidence;
    private LocalDate idIssueDate;
    private LocalDate idExpiryDate;
    private KycStatus status;
    private KycStatus previousStatus;
    private KycRejectReason rejectReason;
    private String rejectNote;
    private int submissionCount;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
    private List<DocumentInfo> documents;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    public static class DocumentInfo {
        private UUID id;
        private String documentType;
        private String fileName;
        private Long fileSize;
        private LocalDateTime uploadedAt;
    }
}
