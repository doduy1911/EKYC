package com.ekyc.kyc.entity;

import com.ekyc.kyc.enums.KycRejectReason;
import com.ekyc.kyc.enums.KycStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "kyc_submissions", indexes = {
        @Index(name = "idx_kyc_user_id", columnList = "user_id"),
        @Index(name = "idx_kyc_status", columnList = "status"),
        @Index(name = "idx_kyc_identity_number", columnList = "identity_number")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Không dùng FK entity để tránh circular dependency giữa module
    // Chỉ lưu userId — gọi qua UserRepository khi cần
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    // ---- Thông tin CCCD ----
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "identity_number", nullable = false, length = 20)
    private String identityNumber;      // Số CCCD

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "nationality", length = 50)
    private String nationality;

    @Column(name = "place_of_origin", length = 255)
    private String placeOfOrigin;       // Quê quán

    @Column(name = "place_of_residence", length = 255)
    private String placeOfResidence;    // Nơi thường trú

    @Column(name = "id_issue_date")
    private LocalDate idIssueDate;      // Ngày cấp

    @Column(name = "id_expiry_date")
    private LocalDate idExpiryDate;     // Ngày hết hạn

    // ---- State machine ----
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private KycStatus status = KycStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", length = 30)
    private KycStatus previousStatus;

    // ---- Review info ----
    @Column(name = "reviewed_by")
    private UUID reviewedBy;            // userId của admin/staff duyệt

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "reject_reason", length = 50)
    private KycRejectReason rejectReason;

    @Column(name = "reject_note", length = 500)
    private String rejectNote;          // Ghi chú thêm khi từ chối

    // ---- Số lần submit ----
    @Column(name = "submission_count")
    @Builder.Default
    private int submissionCount = 0;    // Đếm số lần nộp (tối đa 3 lần)

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    // ---- Relations ----
    @OneToMany(mappedBy = "kycSubmission", cascade = CascadeType.ALL,
               fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<KycDocument> documents = new ArrayList<>();

    @OneToMany(mappedBy = "kycSubmission", cascade = CascadeType.ALL,
               fetch = FetchType.LAZY)
    @Builder.Default
    private List<KycStateLog> stateLogs = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ---- State machine method ----
    public void transitionTo(KycStatus newStatus) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                    String.format("Không thể chuyển từ %s sang %s", this.status, newStatus));
        }
        this.previousStatus = this.status;
        this.status = newStatus;
    }
}
