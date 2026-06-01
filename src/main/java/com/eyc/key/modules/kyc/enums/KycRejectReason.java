package com.eyc.key.modules.kyc.enums;

public enum KycRejectReason {
    BLURRY_IMAGE,           // Ảnh mờ, không đọc được
    EXPIRED_ID,             // CCCD hết hạn
    MISMATCHED_INFO,        // Thông tin không khớp
    INCOMPLETE_DOCUMENT,    // Thiếu mặt trước/sau
    SUSPECTED_FRAUD,        // Nghi ngờ giả mạo
}
