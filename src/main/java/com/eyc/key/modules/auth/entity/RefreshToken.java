package com.eyc.key.modules.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens",indexes = {
        @Index(name = "idx_refresh_token_token" , columnList = "token" , unique = true),
        @Index(name = "idx_refresh_token_user" , columnList = "userId" )
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId",nullable = false)
    private User user;

    @Column(nullable = false, unique = false , length = 512)
    private String token;

    @Column(name = "expries_at" , nullable = false)
    private LocalDateTime expriesAt;

    @Column(name = "revoked")
    @Builder.Default
    private boolean revoked =  false;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Column(name = "device_info", length = 256)
    private String deviceInfo;

    @Column(name = "ip_address", length = 15)
    private String ipAddress;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
