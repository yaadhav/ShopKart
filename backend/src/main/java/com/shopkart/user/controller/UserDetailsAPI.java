package com.shopkart.user.controller;

import com.shopkart.common.util.AuthUtil;
import com.shopkart.user.dto.request.UserDetailsRequest;
import com.shopkart.user.dto.response.UserDetailsResponse;
import com.shopkart.user.service.UserDetailsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user/details")
public class UserDetailsAPI {

    private final UserDetailsService userDetailsService;

    public UserDetailsAPI(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostMapping
    public ResponseEntity<UserDetailsResponse> createUserDetails(@Valid @RequestBody UserDetailsRequest request) {
        Long userId = AuthUtil.getUserIdFromAuth();
        UserDetailsResponse response = userDetailsService.createOrUpdateUserDetails(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<UserDetailsResponse> getUserDetails() {
        Long userId = AuthUtil.getUserIdFromAuth();
        UserDetailsResponse response = userDetailsService.getUserDetails(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<UserDetailsResponse> updateUserDetails(@Valid @RequestBody UserDetailsRequest request) {
        Long userId = AuthUtil.getUserIdFromAuth();
        UserDetailsResponse response = userDetailsService.createOrUpdateUserDetails(userId, request);
        return ResponseEntity.ok(response);
    }
}
