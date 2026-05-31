package com.eyc.key.modules.kyc.enums;

import java.util.Map;
import java.util.Set;

public enum KycStatus {
    DRAFT,              // Đang điền form, chưa nộp
    SUBMITTED,          // Đã nộp, chờ review
    UNDER_REVIEW,       // Admin/Staff đang xem xét
    APPROVED,           // Đã duyệt
    REJECTED,           // Bị từ chối
    RESUBMIT_REQUIRED;  // Yêu cầu nộp lại (từ chối nhẹ)

    private static final Map<KycStatus , Set<KycStatus>> VALID_TRANSITIONS = Map.of(
            DRAFT,Set.of(SUBMITTED),
            SUBMITTED,Set.of(UNDER_REVIEW,REJECTED),
            UNDER_REVIEW,Set.of(APPROVED,REJECTED,RESUBMIT_REQUIRED),
            REJECTED,Set.of(),
            RESUBMIT_REQUIRED,Set.of(SUBMITTED),
            APPROVED,Set.of()
    );

    public boolean canTransitionTo(KycStatus status) {
        return VALID_TRANSITIONS.getOrDefault(this,Set.of()).contains(status);
    }

}
