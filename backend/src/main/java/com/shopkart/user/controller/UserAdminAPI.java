package com.shopkart.user.controller;

import com.shopkart.user.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/users")
@PreAuthorize("hasAnyRole('owner', 'super_admin', 'admin')")
public class UserAdminAPI {

    private final AdminService adminService;

    public UserAdminAPI(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUserByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.getUserByUserId(userId));
    }
}
