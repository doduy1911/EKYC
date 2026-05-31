package com.eyc.key.common.audit;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "audit_logs" , indexes = {
    @Index(name = "idx_audit_userId" , columnList = "userId"),
    @Index(name = "idx_audit_action" , columnList = "action"),
    @Index(name = "idx_audit_createAt" , columnList = "created_at")
})

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Builder
@ToString
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "userId")
    private UUID userId;
    
    @Column(name = "username" ,length = 50)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "action" , length = 50 , nullable = false)
    private AuditAction action;

    @Column(name = "success" , nullable = false)
    private boolean success;

    @Column(name = "description" , nullable = false)
    private String description;

    @Column(name = "ip_address" , length = 45 )
    private String ipAddress;

    @Column(name = "user_agent" , length = 255)
    private String userAgent;

    @CreatedDate
    @Column(name = "created_at" ,updatable = false)
    private LocalDateTime createdAt;


}
