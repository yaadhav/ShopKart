package com.shopkart.auth.controller;

import com.shopkart.auth.dto.CreateAdminRequest;
import com.shopkart.auth.dto.CreateAdminResponse;
import com.shopkart.auth.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminAPI {

    private final AdminService adminService;

    public AdminAPI(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('owner')")
    public ResponseEntity<CreateAdminResponse> createAdmin(@Valid @RequestBody CreateAdminRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createAdmin(request));
    }
}
