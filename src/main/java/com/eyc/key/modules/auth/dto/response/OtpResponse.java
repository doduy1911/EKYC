package com.eyc.key.modules.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OtpResponse {
    private final boolean success;
    private final String message;
    private final int remainingAttempts;

    public static OtpResponse success(String message) {
        return new OtpResponse(true, message, 0);
    }

    public static OtpResponse failed(String message, int remaining) {
        return new OtpResponse(false, message, remaining);
    }
}
