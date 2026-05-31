package com.ekyc.kyc.repository;

import com.ekyc.kyc.entity.KycSubmission;
import com.ekyc.kyc.enums.KycStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface KycSubmissionRepository extends JpaRepository<KycSubmission, UUID> {

    // User xem submission của mình
    Optional<KycSubmission> findByUserIdAndStatus(UUID userId, KycStatus status);

    // Kiểm tra user đã có submission active chưa
    @Query("""
            SELECT COUNT(k) > 0 FROM KycSubmission k
            WHERE k.userId = :userId
              AND k.status NOT IN ('REJECTED')
            """)
    boolean hasActiveSubmission(UUID userId);

    // Lấy submission mới nhất của user
    Optional<KycSubmission> findTopByUserIdOrderByCreatedAtDesc(UUID userId);

    // Admin xem danh sách theo status — có phân trang
    Page<KycSubmission> findByStatus(KycStatus status, Pageable pageable);

    // Admin xem tất cả — có phân trang
    Page<KycSubmission> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // Đếm theo status — cho dashboard admin
    long countByStatus(KycStatus status);

    List<KycSubmission> findByUserId(UUID userId);
}
