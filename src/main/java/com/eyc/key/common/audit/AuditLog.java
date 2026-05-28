package com.eyc.key.common.audit;

import java.time.LocalDate;
import java.util.UUID;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "sucess" , nullable = false)
    private boolean sucess;

    @Column(name = "description" , nullable = false)
    private String description;

    @Column(name = "ip_address" , length = 45 )
    private String ipAddress;

    @Column(name = "user_agen" , length = 255)
    private String userAgent;

    @CreatedDate
    @Column(name = "created_at" ,updatable = false)
    private LocalDate createdAt;

}
