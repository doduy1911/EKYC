package com.eyc.key.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class OtpVerifyException extends  RuntimeException {
    public enum Type { BUSINESS, SYSTEM }
    private final Type type;
    private final int remainingAttempts;

    private OtpVerifyException(String message , int remainingAttempts){
        super(message);
        this.type = Type.BUSINESS;
        this.remainingAttempts = remainingAttempts;
    }

    private OtpVerifyException(String message, Throwable cause) {
        super(message, cause);
        this.type = Type.SYSTEM;
        this.remainingAttempts = 0;
    }

    public static OtpVerifyException business(String message, int remaining) {
        return new OtpVerifyException(message, remaining);
    }

    public static OtpVerifyException system(String message, Throwable cause) {
        return new OtpVerifyException(message, cause);
    }

    public boolean isBusiness() { return type == Type.BUSINESS; }
    public boolean isSystem()   { return type == Type.SYSTEM; }

}
