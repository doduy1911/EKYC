package com.eyc.key.modules.kyc.repository;

import com.eyc.key.modules.kyc.entity.KycDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface KycDocumentRepository extends JpaRepository<KycDocument , UUID> {
}
