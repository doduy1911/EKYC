package com.eyc.key.common.audit;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogsService {
    private final AuditLogRepository auditLogRepository;

    @Async
    public void log(UUID userId,
                    String username,
                    AuditAction auditAction,
                    boolean success,
                    String description){
        String ipAddress = extractIpAddress();
        String userAgen = extractUserAgent();

        AuditLog auditLog = AuditLog.builder()
                .userId(userId)
                .username(username)
                .action(auditAction)
                .sucess(success)
                .description(description)
                .ipAddress(ipAddress)
                .userAgent(userAgen)
                .build();
        auditLogRepository.save(auditLog);

    }

    @Async
    public void log(String username,
                    AuditAction action,
                    boolean success,
                    String description) {
        log(null,username, action, success, description);
    }


    private String extractIpAddress(){
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            if (attrs == null) return "unknown";
            HttpServletRequest request = attrs.getRequest();
            String forwarded = request.getHeader("x-forwarded-for");

            if (forwarded == null && !forwarded.isEmpty()){
                return forwarded.split(",")[0].trim();
            }
            return request.getRemoteAddr();
        }catch (Exception e){
            return "unknown";
        }
    }

    private String extractUserAgent(){
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            if (attrs == null) return "unknown";
            String userAgent = attrs.getRequest().getHeader("User-Agent");
            if (userAgent == null) return "unknown";
            return userAgent.length() > 255
                    ? userAgent.substring(0, 255)
                    : userAgent;
        } catch (Exception e) {
            return "unknown";
        }
    }
}
