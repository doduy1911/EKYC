package com.eyc.key.modules.kyc.dto.request;

import com.eyc.key.modules.kyc.enums.KycRejectReason;
import com.eyc.key.modules.kyc.enums.KycStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class KycReviewRequest {
    @NotNull(message = "Trạng thái không được trống")
    private KycStatus decision;     // APPROVED, REJECTED, RESUBMIT_REQUIRED

    private KycRejectReason rejectReason;   // Bắt buộc nếu REJECTED

    private String note;
}
