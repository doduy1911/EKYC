package com.ekyc.kyc.entity;

import com.ekyc.kyc.enums.KycStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "kyc_state_logs", indexes = {
        @Index(name = "idx_kyc_state_log_submission", columnList = "kyc_submission_id")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycStateLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kyc_submission_id", nullable = false)
    private KycSubmission kycSubmission;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", length = 30)
    private KycStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false, length = 30)
    private KycStatus toStatus;

    // Ai trigger transition này — userId của user hoặc admin
    @Column(name = "triggered_by")
    private UUID triggeredBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "triggered_by_role", length = 20)
    private TriggeredByRole triggeredByRole;

    // Ghi chú thêm — lý do reject, note của admin
    @Column(name = "note", length = 500)
    private String note;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum TriggeredByRole {
        USER,
        STAFF,
        ADMIN,
        SYSTEM
    }
}
