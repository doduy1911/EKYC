package com.eyc.key.common.audit;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogsService {
    private final AuditLogRepository auditLogRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(UUID userId,
                    String username,
                    AuditAction auditAction,
                    boolean success,
                    String ipAddress,
                    String userAgen,
                    String description){

        AuditLog auditLog = AuditLog.builder()
                .userId(userId)
                .username(username)
                .action(auditAction)
                .success(success)
                .ipAddress(ipAddress)
                .userAgent(userAgen)
                .description(description)
                .build();
        System.out.println(auditLog);
        auditLogRepository.save(auditLog);
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String username,
                    AuditAction action,
                    boolean success,
                    String ipAddress,
                    String userAgen,
                    String description) {
        log(null,username, action, success , ipAddress, userAgen ,description);
    }

}
