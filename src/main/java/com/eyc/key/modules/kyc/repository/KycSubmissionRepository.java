package com.eyc.key.modules.kyc.repository;


import com.eyc.key.modules.kyc.entity.KycSubmission;
import com.eyc.key.modules.kyc.enums.KycStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface KycSubmissionRepository extends JpaRepository<KycSubmission , UUID> {
    Optional<KycSubmission> findByUserIdAndStatus(UUID userId , KycStatus status);

    @Query("""
            SELECT COUNT(k) > 0 FROM KycSubmission k
            WHERE k.userId = :userId
              AND k.status NOT IN ('REJECTED')
            """)
    boolean hasActiveSubmission(UUID userId);

    Optional<KycSubmission> findTopByUserIdOrderByCreatedAtDesc(UUID userId);


}
