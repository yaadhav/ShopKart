package com.shopkart.order.controller;

import com.shopkart.order.dto.request.FeeDetailsRequest;
import com.shopkart.order.service.FeeDetailsService;
import com.shopkart.user.util.AuthUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/fees")
@PreAuthorize("hasAnyRole('owner', 'super_admin')")
public class AdminFeeAPI {

    private final FeeDetailsService feeDetailsService;

    public AdminFeeAPI(FeeDetailsService feeDetailsService) {
        this.feeDetailsService = feeDetailsService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getFeeDetails() {
        return ResponseEntity.ok(feeDetailsService.getFeeDetails());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrUpdateFeeDetails(@RequestBody FeeDetailsRequest request) {
        Long userId = AuthUtil.getUserIdFromJwt();
        return ResponseEntity.status(HttpStatus.CREATED).body(feeDetailsService.createOrUpdateFeeDetails(userId, request));
    }
}
