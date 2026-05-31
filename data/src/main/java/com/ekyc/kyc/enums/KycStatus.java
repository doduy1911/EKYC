package com.ekyc.kyc.enums;

import java.util.Set;
import java.util.Map;

public enum KycStatus {
    DRAFT,              // Đang điền form, chưa nộp
    SUBMITTED,          // Đã nộp, chờ review
    UNDER_REVIEW,       // Admin/Staff đang xem xét
    APPROVED,           // Đã duyệt
    REJECTED,           // Bị từ chối
    RESUBMIT_REQUIRED;  // Yêu cầu nộp lại (từ chối nhẹ)

    // State machine — định nghĩa transition hợp lệ
    private static final Map<KycStatus, Set<KycStatus>> VALID_TRANSITIONS = Map.of(
            DRAFT,             Set.of(SUBMITTED),
            SUBMITTED,         Set.of(UNDER_REVIEW, REJECTED),
            UNDER_REVIEW,      Set.of(APPROVED, REJECTED, RESUBMIT_REQUIRED),
            REJECTED,          Set.of(),               // Terminal state
            RESUBMIT_REQUIRED, Set.of(SUBMITTED),      // User có thể nộp lại
            APPROVED,          Set.of()                // Terminal state
    );

    public boolean canTransitionTo(KycStatus newStatus) {
        return VALID_TRANSITIONS.getOrDefault(this, Set.of()).contains(newStatus);
    }
}
