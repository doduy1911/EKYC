package com.ekyc.kyc.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "kyc_documents")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kyc_submission_id", nullable = false)
    private KycSubmission kycSubmission;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 20)
    private DocumentType documentType;  // FRONT, BACK, SELFIEArrayList

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;            // Tên file gốc

    @Column(name = "stored_path", nullable = false, length = 500)
    private String storedPath;          // Đường dẫn lưu trên server

    @Column(name = "file_size")
    private Long fileSize;              // Bytes

    @Column(name = "content_type", length = 50)
    private String contentType;         // image/jpeg, image/png

    @Column(name = "checksum", length = 64)
    private String checksum;            // SHA-256 để verify file toàn vẹn

    @CreatedDate
    @Column(name = "uploaded_at", updatable = false)
    private LocalDateTime uploadedAt;

    public enum DocumentType {
        FRONT,      // Mặt trước CCCD
        BACK,       // Mặt sau CCCD
        SELFIE      // Ảnh selfie cầm CCCD
    }
}
