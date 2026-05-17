package com.eyc.key.modules.auth.repository;

import com.eyc.key.modules.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken , UUID> {
    Optional<RefreshToken> findByToken(String token);
    @Modifying
    @Query("update RefreshToken rt set rt.revoked = true , rt.revokedAt = CURRENT_TIMESTAMP where rt.user.userId = :userId and rt.revoked = false ")
    void revokeAllByUserId(UUID userId);

    @Modifying
    @Query("delete from RefreshToken rt where rt.revoked = true or rt.expriesAt < CURRENT_TIMESTAMP ")
    void deleteExpiredAndRevoked();
}
