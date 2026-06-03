package com.eyc.key.modules.kyc.entity;

import com.eyc.key.modules.kyc.enums.KycStatus;
import com.eyc.key.modules.kyc.enums.TriggeredByRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "kyc_state_log", indexes = {
        @Index(name = "idx_kyc_state_log_submission" , columnList = "kyc_submission_id")
})
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class KycStateLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kyc_submission_id", nullable = false)
    private KycSubmission kycSubmission;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status" , length = 30)
    private KycStatus kycStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false, length = 30)
    private KycStatus toStatus;

    @Column(name = "triggered_by" )
    private UUID triggeredBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "triigered_by_role", length = 20)
    private TriggeredByRole triggeredByRole;

    @Column(name = "note" , length = 255 )
    private String note;

    @CreatedDate
    @Column(name = "created_at" , updatable = false)
    private LocalDateTime createdAt;

}
