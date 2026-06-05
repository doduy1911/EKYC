package com.eyc.key.modules.kyc.controller;

import com.eyc.key.modules.kyc.dto.Response.KycResponse;
import com.eyc.key.modules.kyc.enums.KycStatus;
import com.eyc.key.modules.kyc.service.KycService;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/staff/kyc")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('STAFF','ADMIN')")
public class KycAdminController {
    private final KycService kycService;

    @GetMapping
    public ResponseEntity<Page<KycResponse>> getAll(
            @RequestParam(required = false) KycStatus status,
            @PageableDefault(size = 20 ,sort = "CreatedAt") Pageable pageable
    ){
        return null;
    }

}
