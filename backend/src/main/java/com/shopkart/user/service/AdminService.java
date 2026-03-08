package com.shopkart.user.service;

import com.shopkart.user.dto.enums.Gender;
import com.shopkart.user.dto.request.CreateAdminRequest;
import com.shopkart.user.dto.response.CreateAdminResponse;
import com.shopkart.user.dto.enums.Role;
import com.shopkart.user.model.UserDetailsEntity;
import com.shopkart.user.model.UserEntity;
import com.shopkart.user.repo.UserDetailsRepo;
import com.shopkart.user.repo.UserRepo;
import com.shopkart.user.util.AuthExceptionStore;
import com.shopkart.user.util.AuthUtil;
import com.shopkart.user.util.UserConstants.Keys;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class AdminService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789!@#$%";
    private static final int PASSWORD_LENGTH = 16;
    private static final Set<String> VALID_ADMIN_ROLES = Set.of(
            Role.SUPER_ADMIN.name, Role.ADMIN.name, Role.ORDER_ADMIN.name, Role.PRODUCT_ADMIN.name
    );
    private static final Set<String> SUPER_ADMIN_CREATABLE_ROLES = Set.of(
            Role.ADMIN.name, Role.ORDER_ADMIN.name, Role.PRODUCT_ADMIN.name
    );

    private final UserRepo userRepo;
    private final UserDetailsRepo userDetailsRepo;
    private final PasswordEncoder passwordEncoder;

    public AdminService(UserRepo userRepo, UserDetailsRepo userDetailsRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.userDetailsRepo = userDetailsRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public CreateAdminResponse createAdmin(CreateAdminRequest request) {
        if(userRepo.existsByEmail(request.getEmail())) {
            throw AuthExceptionStore.USER_ALREADY_EXISTS.exception();
        }

        String requestedRole = request.getRole();
        if(!VALID_ADMIN_ROLES.contains(requestedRole)) {
            throw AuthExceptionStore.INVALID_ROLE.exception();
        }

        String callerRole = AuthUtil.getRoleFromJwt();
        if(Role.SUPER_ADMIN.name.equals(callerRole) && !SUPER_ADMIN_CREATABLE_ROLES.contains(requestedRole)) {
            throw AuthExceptionStore.INSUFFICIENT_PERMISSION.exception();
        }

        int roleCode = Role.getCode(requestedRole);
        String rawPassword = generatePassword();

        UserEntity admin = UserEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(rawPassword))
                .role(roleCode)
                .build();

        userRepo.save(admin);

        return CreateAdminResponse.builder()
                .email(admin.getEmail())
                .name(admin.getName())
                .role(Role.getName(admin.getRole()))
                .generatedPassword(rawPassword)
                .build();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getUserByUserId(Long userId) {
        UserEntity user = userRepo.findById(userId)
                .orElseThrow(AuthExceptionStore.USER_NOT_FOUND::exception);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put(Keys.USER_ID, user.getUserId());
        response.put(Keys.NAME, user.getName());
        response.put(Keys.EMAIL, user.getEmail());
        response.put(Keys.ROLE, Role.getName(user.getRole()));
        response.put(Keys.ROLE + Keys.FORMATTED_SUFFIX, Role.getDisplayName(user.getRole()));

        Optional<UserDetailsEntity> details = userDetailsRepo.findByUserId(userId);
        if(details.isPresent()) {
            UserDetailsEntity detailsEntity = details.get();
            response.put(Keys.PHONE_NUMBER, detailsEntity.getPhoneNumber());
            if(detailsEntity.getGender() != null) {
                response.put(Keys.GENDER, Gender.getName(detailsEntity.getGender()));
                response.put(Keys.GENDER + Keys.FORMATTED_SUFFIX, Gender.getDisplayName(detailsEntity.getGender()));
            }
            response.put(Keys.DATE_OF_BIRTH, detailsEntity.getDateOfBirth());
        }

        return response;
    }

    private String generatePassword() {
        StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);
        for(int i = 0; i < PASSWORD_LENGTH; i++) {
            sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}
