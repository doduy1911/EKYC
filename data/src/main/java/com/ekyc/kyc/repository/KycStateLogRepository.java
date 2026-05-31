package com.ekyc.kyc.repository;

import com.ekyc.kyc.entity.KycStateLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface KycStateLogRepository extends JpaRepository<KycStateLog, UUID> {

    // Lấy toàn bộ lịch sử state của 1 submission — theo thời gian
    List<KycStateLog> findByKycSubmissionIdOrderByCreatedAtAsc(UUID submissionId);
}
