package com.eyc.key.modules.auth.repository;

import com.eyc.key.common.enums.OtpType;
import com.eyc.key.modules.auth.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OtpRepository extends JpaRepository<OtpVerification, UUID> {
    @Query("""
        select o from OtpVerification o
            where o.user.userId = :userid
                and o.otpType = :type
                and o.verified = false 
                and o.expiresAt > CURRENT_TIMESTAMP 
            order by o.createdAt DESC
            limit 1
    """)
    Optional<OtpVerification> findLatestValidOtp(UUID userId, OtpType type);

    @Modifying
    @Query("delete from OtpVerification o where o.user.userId = :userId and o.otpType = :type and o.verified = false ")
    void deleteUnverifiedByUserAndType(UUID userId, OtpType type);


}
