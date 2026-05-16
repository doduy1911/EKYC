package com.eyc.key.modules.auth.repository;

import com.eyc.key.modules.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository  extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

// số lần thử không thành công
    @Modifying
    @Query("update User u set u.failedLoginAttempts =  u.failedLoginAttempts + 1 where u.userId = :userId")
    void incrementFailedAttempts(UUID userId);
// đặt lại số lần thử thất bại
    @Modifying
    @Query("update User u set u.failedLoginAttempts = 0 , u.lockedUntil = null where u.userId = :userId")
    void resetFailedAttempts(UUID userId);
 }
