package com.shopkart.user.service;

import com.shopkart.user.dto.response.AuthResponse;
import com.shopkart.user.dto.request.LoginRequest;
import com.shopkart.user.dto.request.RegisterRequest;
import com.shopkart.user.dto.enums.Role;
import com.shopkart.user.model.UserEntity;
import com.shopkart.user.repo.UserRepo;
import com.shopkart.common.exception.ShopKartException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        testUser = UserEntity.builder()
                .userId(1L)
                .name("Test User")
                .email("test@shopkart.com")
                .password("encoded_password")
                .role(Role.USER.code)
                .build();
    }

    @Test
    void register_withValidRequest_returnsAuthResponse() {
        RegisterRequest request = new RegisterRequest("Test User", "test@shopkart.com", "password123");

        when(userRepo.existsByEmail("test@shopkart.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(userRepo.save(any(UserEntity.class))).thenReturn(testUser);
        when(jwtService.generateToken(any(), anyString(), anyString())).thenReturn("jwt-token");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("test@shopkart.com", response.getEmail());
        assertEquals("Test User", response.getName());
        assertEquals("user", response.getRole());
        verify(userRepo).save(any(UserEntity.class));
    }

    @Test
    void register_withDuplicateEmail_throwsConflict() {
        RegisterRequest request = new RegisterRequest("Test User", "test@shopkart.com", "password123");
        when(userRepo.existsByEmail("test@shopkart.com")).thenReturn(true);

        ShopKartException ex = assertThrows(ShopKartException.class, () -> authService.register(request));

        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        assertEquals("AUTH_003", ex.getErrorCode());
        verify(userRepo, never()).save(any());
    }

    @Test
    void login_withValidCredentials_returnsAuthResponse() {
        LoginRequest request = new LoginRequest("test@shopkart.com", "password123");

        when(userRepo.findByEmail("test@shopkart.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encoded_password")).thenReturn(true);
        when(jwtService.generateToken(1L, "test@shopkart.com", "user")).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("test@shopkart.com", response.getEmail());
    }

    @Test
    void login_withWrongEmail_throwsUnauthorized() {
        LoginRequest request = new LoginRequest("unknown@shopkart.com", "password123");
        when(userRepo.findByEmail("unknown@shopkart.com")).thenReturn(Optional.empty());

        ShopKartException ex = assertThrows(ShopKartException.class, () -> authService.login(request));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
        assertEquals("AUTH_002", ex.getErrorCode());
    }

    @Test
    void login_withWrongPassword_throwsUnauthorized() {
        LoginRequest request = new LoginRequest("test@shopkart.com", "wrong_password");

        when(userRepo.findByEmail("test@shopkart.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrong_password", "encoded_password")).thenReturn(false);

        ShopKartException ex = assertThrows(ShopKartException.class, () -> authService.login(request));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
        assertEquals("AUTH_002", ex.getErrorCode());
        verify(jwtService, never()).generateToken(anyLong(), anyString(), anyString());
    }
}
