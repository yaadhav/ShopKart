package com.shopkart.order.internalagent;

import com.shopkart.order.internalagent.resource.AddressResource;
import com.shopkart.user.model.AddressEntity;
import com.shopkart.user.repo.AddressRepo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AddressAgent {

    private final AddressRepo addressRepo;

    public AddressAgent(AddressRepo addressRepo) {
        this.addressRepo = addressRepo;
    }

    public AddressResource getDefaultAddress(Long userId) {
        return addressRepo.findByUserIdAndIsDefaultTrue(userId)
                .map(this::toResource)
                .orElse(null);
    }

    public List<AddressResource> getAddresses(Long userId) {
        return addressRepo.findByUserId(userId).stream()
                .map(this::toResource)
                .collect(Collectors.toList());
    }

    public AddressResource getAddress(Long userId, Long addressId) {
        return addressRepo.findByAddressIdAndUserId(addressId, userId)
                .map(this::toResource)
                .orElse(null);
    }

    private AddressResource toResource(AddressEntity entity) {
        return AddressResource.builder()
                .addressId(entity.getAddressId())
                .name(entity.getName())
                .contactNumber(entity.getContactNumber())
                .firstLine(entity.getFirstLine())
                .secondLine(entity.getSecondLine())
                .landmark(entity.getLandmark())
                .city(entity.getCity())
                .state(entity.getState())
                .pincode(entity.getPincode())
                .isDefault(entity.getIsDefault())
                .build();
    }
}
