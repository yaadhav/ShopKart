package com.shopkart.auth.service;

import com.shopkart.auth.dto.AuthResponse;
import com.shopkart.auth.dto.LoginRequest;
import com.shopkart.auth.dto.RegisterRequest;
import com.shopkart.auth.model.Role;
import com.shopkart.auth.model.UserEntity;
import com.shopkart.auth.repo.UserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepo userRepo,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepo.existsByEmail(request.getEmail())) {
            throw AuthExceptionStore.USER_ALREADY_EXISTS.exception();
        }

        UserEntity user = UserEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER.code)
                .build();

        userRepo.save(user);

        String token = jwtService.generateToken(user.getUserId(), user.getEmail(), Role.getName(user.getRole()));
        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .name(user.getName())
                .role(Role.getName(user.getRole()))
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        UserEntity user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> AuthExceptionStore.INVALID_CREDENTIALS.exception());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw AuthExceptionStore.INVALID_CREDENTIALS.exception();
        }

        String token = jwtService.generateToken(user.getUserId(), user.getEmail(), Role.getName(user.getRole()));
        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .name(user.getName())
                .role(Role.getName(user.getRole()))
                .build();
    }
}
