package com.shopkart.user.service;

import com.shopkart.common.exception.ShopKartException;
import com.shopkart.user.dto.enums.Role;
import com.shopkart.user.model.UserDetailsEntity;
import com.shopkart.user.model.UserEntity;
import com.shopkart.user.repo.UserDetailsRepo;
import com.shopkart.user.repo.UserRepo;
import com.shopkart.user.util.UserConstants.Keys;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceGetUserTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private UserDetailsRepo userDetailsRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminService adminService;

    @Test
    void getUserByUserId_withExistingUserAndDetails_returnsFullResponse() {
        UserEntity user = UserEntity.builder()
                .userId(1L)
                .name("John Doe")
                .email("john@shopkart.com")
                .role(Role.USER.code)
                .build();
        UserDetailsEntity details = UserDetailsEntity.builder()
                .userId(1L)
                .phoneNumber("9876543210")
                .gender(1)
                .dateOfBirth(LocalDate.of(1995, 6, 15))
                .build();

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(userDetailsRepo.findByUserId(1L)).thenReturn(Optional.of(details));

        Map<String, Object> result = adminService.getUserByUserId(1L);

        assertEquals(1L, result.get(Keys.USER_ID));
        assertEquals("John Doe", result.get(Keys.NAME));
        assertEquals("john@shopkart.com", result.get(Keys.EMAIL));
        assertEquals("user", result.get(Keys.ROLE));
        assertEquals("User", result.get(Keys.ROLE + Keys.FORMATTED_SUFFIX));
        assertEquals("9876543210", result.get(Keys.PHONE_NUMBER));
        assertEquals("male", result.get(Keys.GENDER));
        assertEquals("Male", result.get(Keys.GENDER + Keys.FORMATTED_SUFFIX));
        assertEquals(LocalDate.of(1995, 6, 15), result.get(Keys.DATE_OF_BIRTH));
    }

    @Test
    void getUserByUserId_withExistingUserNoDetails_returnsBasicResponse() {
        UserEntity user = UserEntity.builder()
                .userId(2L)
                .name("Jane Admin")
                .email("jane@shopkart.com")
                .role(Role.ADMIN.code)
                .build();

        when(userRepo.findById(2L)).thenReturn(Optional.of(user));
        when(userDetailsRepo.findByUserId(2L)).thenReturn(Optional.empty());

        Map<String, Object> result = adminService.getUserByUserId(2L);

        assertEquals(2L, result.get(Keys.USER_ID));
        assertEquals("Jane Admin", result.get(Keys.NAME));
        assertEquals("jane@shopkart.com", result.get(Keys.EMAIL));
        assertEquals("admin", result.get(Keys.ROLE));
        assertEquals("Admin", result.get(Keys.ROLE + Keys.FORMATTED_SUFFIX));
        assertFalse(result.containsKey(Keys.PHONE_NUMBER));
        assertFalse(result.containsKey(Keys.GENDER));
        assertFalse(result.containsKey(Keys.DATE_OF_BIRTH));
    }

    @Test
    void getUserByUserId_withDetailsButNullGender_excludesGenderFields() {
        UserEntity user = UserEntity.builder()
                .userId(3L)
                .name("No Gender")
                .email("ng@shopkart.com")
                .role(Role.USER.code)
                .build();
        UserDetailsEntity details = UserDetailsEntity.builder()
                .userId(3L)
                .phoneNumber("1234567890")
                .gender(null)
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .build();

        when(userRepo.findById(3L)).thenReturn(Optional.of(user));
        when(userDetailsRepo.findByUserId(3L)).thenReturn(Optional.of(details));

        Map<String, Object> result = adminService.getUserByUserId(3L);

        assertEquals("1234567890", result.get(Keys.PHONE_NUMBER));
        assertFalse(result.containsKey(Keys.GENDER));
        assertFalse(result.containsKey(Keys.GENDER + Keys.FORMATTED_SUFFIX));
        assertEquals(LocalDate.of(2000, 1, 1), result.get(Keys.DATE_OF_BIRTH));
    }

    @Test
    void getUserByUserId_withNonExistentUser_throwsUserNotFound() {
        when(userRepo.findById(999L)).thenReturn(Optional.empty());

        ShopKartException ex = assertThrows(ShopKartException.class, () -> adminService.getUserByUserId(999L));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertEquals("AUTH_001", ex.getErrorCode());
    }

    @Test
    void getUserByUserId_withSuperAdminRole_returnsCorrectFormattedRole() {
        UserEntity user = UserEntity.builder()
                .userId(4L)
                .name("Super Admin")
                .email("super@shopkart.com")
                .role(Role.SUPER_ADMIN.code)
                .build();

        when(userRepo.findById(4L)).thenReturn(Optional.of(user));
        when(userDetailsRepo.findByUserId(4L)).thenReturn(Optional.empty());

        Map<String, Object> result = adminService.getUserByUserId(4L);

        assertEquals("super_admin", result.get(Keys.ROLE));
        assertEquals("Super Admin", result.get(Keys.ROLE + Keys.FORMATTED_SUFFIX));
    }
}
