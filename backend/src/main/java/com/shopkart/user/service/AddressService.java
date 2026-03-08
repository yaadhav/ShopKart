package com.shopkart.user.service;

import com.shopkart.user.dto.request.AddressRequest;
import com.shopkart.user.dto.response.AddressResponse;
import com.shopkart.user.model.AddressEntity;
import com.shopkart.user.repo.AddressRepo;
import com.shopkart.user.repo.UserRepo;
import com.shopkart.user.util.AuthExceptionStore;
import com.shopkart.user.util.UserExceptionStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AddressService {

    private final AddressRepo addressRepo;
    private final UserRepo userRepo;

    public AddressService(AddressRepo addressRepo, UserRepo userRepo) {
        this.addressRepo = addressRepo;
        this.userRepo = userRepo;
    }

    @Transactional
    public AddressResponse createAddress(Long userId, AddressRequest request) {
        if(!userRepo.existsById(userId)) {
            throw AuthExceptionStore.USER_NOT_FOUND.exception();
        }

        if(Boolean.TRUE.equals(request.getIsDefault())) {
            addressRepo.unsetDefaultAddressForUser(userId);
        }

        AddressEntity address = AddressEntity.builder()
                .userId(userId)
                .name(request.getName())
                .contactNumber(request.getContactNumber())
                .firstLine(request.getFirstLine())
                .secondLine(request.getSecondLine())
                .landmark(request.getLandmark())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .isDefault(Optional.ofNullable(request.getIsDefault()).orElse(false))
                .build();

        address = addressRepo.save(address);

        return mapToResponse(address);
    }

    public List<AddressResponse> getUserAddresses(Long userId) {
        List<AddressEntity> addresses = addressRepo.findByUserId(userId);
        return addresses.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public AddressResponse getAddress(Long userId, Long addressId) {
        AddressEntity address = addressRepo.findByAddressIdAndUserId(addressId, userId)
                .orElseThrow(UserExceptionStore.ADDRESS_NOT_FOUND::exception);
        return mapToResponse(address);
    }

    @Transactional
    public AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request) {
        AddressEntity address = addressRepo.findByAddressIdAndUserId(addressId, userId)
                .orElseThrow(UserExceptionStore.ADDRESS_NOT_FOUND::exception);

        if(Boolean.TRUE.equals(request.getIsDefault()) && !address.getIsDefault()) {
            addressRepo.unsetDefaultAddressForUser(userId);
        }

        address.setName(request.getName());
        address.setContactNumber(request.getContactNumber());
        address.setFirstLine(request.getFirstLine());
        address.setSecondLine(request.getSecondLine());
        address.setLandmark(request.getLandmark());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPincode(request.getPincode());
        address.setIsDefault(Optional.ofNullable(request.getIsDefault()).orElse(false));

        address = addressRepo.save(address);

        return mapToResponse(address);
    }

    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        AddressEntity address = addressRepo.findByAddressIdAndUserId(addressId, userId)
                .orElseThrow(UserExceptionStore.ADDRESS_NOT_FOUND::exception);
        addressRepo.delete(address);
    }

    @Transactional
    public AddressResponse setDefaultAddress(Long userId, Long addressId) {
        AddressEntity address = addressRepo.findByAddressIdAndUserId(addressId, userId)
                .orElseThrow(UserExceptionStore.ADDRESS_NOT_FOUND::exception);

        addressRepo.unsetDefaultAddressForUser(userId);

        address.setIsDefault(true);
        address = addressRepo.save(address);

        return mapToResponse(address);
    }

    public AddressResponse getDefaultAddress(Long userId) {
        AddressEntity address = addressRepo.findByUserIdAndIsDefaultTrue(userId)
                .orElseThrow(UserExceptionStore.ADDRESS_NOT_FOUND::exception);
        return mapToResponse(address);
    }

    private AddressResponse mapToResponse(AddressEntity entity) {
        return AddressResponse.builder()
                .addressId(entity.getAddressId())
                .userId(entity.getUserId())
                .name(entity.getName())
                .contactNumber(entity.getContactNumber())
                .firstLine(entity.getFirstLine())
                .secondLine(entity.getSecondLine())
                .landmark(entity.getLandmark())
                .city(entity.getCity())
                .state(entity.getState())
                .pincode(entity.getPincode())
                .isDefault(entity.getIsDefault())
                .createdTime(entity.getCreatedTime())
                .updatedTime(entity.getUpdatedTime())
                .build();
    }
}
