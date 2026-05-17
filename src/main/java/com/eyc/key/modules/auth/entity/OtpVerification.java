package com.eyc.key.modules.auth.entity;

import com.eyc.key.common.enums.OtpType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "otp_verification" , indexes = {
        @Index(name="idex_otp_eamil" , columnList="email"),
        @Index(name = "idex_otp_userID",columnList = "userId")
})
public class OtpVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID otpId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId",nullable = false)
    private User user;

    @Column(nullable = false , length=100)
    private String email;

    @Column(name = "otp_hash" , nullable = false)
    private String otpHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OtpType otpType;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "attempts" , nullable = false)
    @Builder.Default
    private Integer attempts = 0;

    @Column(name="max_attempts", nullable = false)
    @Builder.Default
    private int maxAttempts = 5;

    @Column(name = "verified")
    @Builder.Default
    private boolean verified = false;

    @Column(name = "verified_at")
    @Builder.Default
    private LocalDateTime verifiedAt = LocalDateTime.now();

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public boolean isExpired(){
        return LocalDateTime.now().isAfter(expiresAt);
    }
    public boolean isExceededAttempts(){
        return attempts >= maxAttempts;
    }

    public boolean isUsable(){
        return !verified && !isExpired() && !isExceededAttempts();
    }


}
