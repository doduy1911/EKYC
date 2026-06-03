package com.eyc.key.modules.kyc.entity;


import com.eyc.key.modules.kyc.enums.KycRejectReason;
import com.eyc.key.modules.kyc.enums.KycStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.cglib.core.Local;
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
        @Index(name = "idx_kyc_userId", columnList = "userId"),
        @Index(name = "idx_kyc_status" , columnList = "status"),
        @Index(name = "idx_kyc_identity_number", columnList = "identity_number")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class KycSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "userId", nullable = false)
    private UUID userId;

    @Column(name = "fullName", nullable = false, length = 100)
    private String fullName;

    @Column(name = "identity_number", nullable = false, length = 20)
    private String identityNumber; // số căn cước công nhân

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth; // sinh nhật

    @Column(name = "gender", length = 10)
    private String gender; // giới tính

    @Column(name = "nationality", length = 50)
    private String nationality;  // quốc tịch

    @Column(name = "place_of_origin", length = 255)
    private String placeOfOrigin; // Nơi thường chú

    @Column(name = "id_issue_date")
    private LocalDate idIssueDate;  // ngày cấp

    @Column(name = "id_expiry_date")
    private LocalDate idExpiryDate;  // ngày hết hạn

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private KycStatus status = KycStatus.DRAFT; // Trạng thái hiện tại mặc định là bản nháp khi mới nộp

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status")
    private KycStatus previousStatus;  // Trạng thái tiếp theo

    @Column(name = "reviewed_by")
    private UUID reviewedBy;  // UUID của người review
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "reject_reason", length = 50)
    private KycRejectReason rejectReason; // tại sao lại bị reject

    @Column(name = "reject_note", length = 500)
    private String rejectNote; // Ghi chú thêm khi từ chối

    @Column(name = "submission_count")
    @Builder.Default
    private int submissionCount = 0; // đêm số lần nộp tối đa (Tối đa 3 lần )

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt; // gian gian noopj


    @OneToMany(mappedBy = "kycSubmission", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<KycDocument> documents = new ArrayList<>();

    @OneToMany(mappedBy = "kycSubmission", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<KycStateLog> stateLogs = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at" , updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "update_at")
    private LocalDateTime updateAt;


    public void transitionTo(KycStatus newStatus){
        if (!this.status.canTransitionTo(newStatus)){
            throw new IllegalStateException(
                    String.format("không thể chuyển từ %s sàn %s ", this.status, newStatus)
            );}
        this.previousStatus = this.status;
        this.status = newStatus;
    }

}

