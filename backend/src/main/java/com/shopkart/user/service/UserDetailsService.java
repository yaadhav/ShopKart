package com.shopkart.user.service;

import com.shopkart.user.dto.request.UserDetailsRequest;
import com.shopkart.user.dto.response.UserDetailsResponse;
import com.shopkart.user.model.UserDetailsEntity;
import com.shopkart.user.repo.UserDetailsRepo;
import com.shopkart.user.repo.UserRepo;
import com.shopkart.user.util.AuthExceptionStore;
import com.shopkart.user.util.UserExceptionStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsService {

    private final UserDetailsRepo userDetailsRepo;
    private final UserRepo userRepo;

    public UserDetailsService(UserDetailsRepo userDetailsRepo, UserRepo userRepo) {
        this.userDetailsRepo = userDetailsRepo;
        this.userRepo = userRepo;
    }

    @Transactional
    public UserDetailsResponse createOrUpdateUserDetails(Long userId, UserDetailsRequest request) {
        if(!userRepo.existsById(userId)) {
            throw AuthExceptionStore.USER_NOT_FOUND.exception();
        }

        UserDetailsEntity userDetails = userDetailsRepo.findByUserId(userId)
                .orElse(UserDetailsEntity.builder()
                        .userId(userId)
                        .build());

        userDetails.setPhoneNumber(request.getPhoneNumber());
        userDetails.setGender(request.getGender());
        userDetails.setDateOfBirth(request.getDateOfBirth());

        userDetails = userDetailsRepo.save(userDetails);

        return mapToResponse(userDetails);
    }

    public UserDetailsResponse getUserDetails(Long userId) {
        UserDetailsEntity userDetails = userDetailsRepo.findByUserId(userId)
                .orElseThrow(UserExceptionStore.USER_DETAILS_NOT_FOUND::exception);
        return mapToResponse(userDetails);
    }

    private UserDetailsResponse mapToResponse(UserDetailsEntity entity) {
        return UserDetailsResponse.builder()
                .userId(entity.getUserId())
                .phoneNumber(entity.getPhoneNumber())
                .gender(entity.getGender())
                .dateOfBirth(entity.getDateOfBirth())
                .createdTime(entity.getCreatedTime())
                .updatedTime(entity.getUpdatedTime())
                .build();
    }
}
