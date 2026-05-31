package com.ekyc.kyc.repository;

import com.ekyc.kyc.entity.KycDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface KycDocumentRepository extends JpaRepository<KycDocument, UUID> {
    List<KycDocument> findByKycSubmissionId(UUID submissionId);
    void deleteByKycSubmissionId(UUID submissionId);
}
