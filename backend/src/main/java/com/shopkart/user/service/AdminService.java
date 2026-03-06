package com.shopkart.user.service;

import com.shopkart.user.dto.request.CreateAdminRequest;
import com.shopkart.user.dto.response.CreateAdminResponse;
import com.shopkart.user.dto.enums.Role;
import com.shopkart.user.model.UserEntity;
import com.shopkart.user.repo.UserRepo;
import com.shopkart.user.util.AuthExceptionStore;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
public class AdminService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789!@#$%";
    private static final int PASSWORD_LENGTH = 16;

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public AdminService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public CreateAdminResponse createAdmin(CreateAdminRequest request) {
        if (userRepo.existsByEmail(request.getEmail())) {
            throw AuthExceptionStore.USER_ALREADY_EXISTS.exception();
        }

        String rawPassword = generatePassword();

        UserEntity admin = UserEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(rawPassword))
                .role(Role.ADMIN.code)
                .build();

        userRepo.save(admin);

        return CreateAdminResponse.builder()
                .email(admin.getEmail())
                .name(admin.getName())
                .role(Role.getName(admin.getRole()))
                .generatedPassword(rawPassword)
                .build();
    }

    private String generatePassword() {
        StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}
