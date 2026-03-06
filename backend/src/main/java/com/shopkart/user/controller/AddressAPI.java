package com.shopkart.user.controller;

import com.shopkart.common.util.AuthUtil;
import com.shopkart.user.dto.request.AddressRequest;
import com.shopkart.user.dto.response.AddressResponse;
import com.shopkart.user.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressAPI {

    private final AddressService addressService;

    public AddressAPI(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping
    public ResponseEntity<AddressResponse> createAddress(@Valid @RequestBody AddressRequest request) {
        Long userId = AuthUtil.getUserIdFromAuth();
        AddressResponse response = addressService.createAddress(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AddressResponse>> getUserAddresses() {
        Long userId = AuthUtil.getUserIdFromAuth();
        List<AddressResponse> addresses = addressService.getUserAddresses(userId);
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressResponse> getAddress(@PathVariable("id") Long addressId) {
        Long userId = AuthUtil.getUserIdFromAuth();
        AddressResponse response = addressService.getAddress(userId, addressId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressResponse> updateAddress(
            @PathVariable("id") Long addressId,
            @Valid @RequestBody AddressRequest request) {
        Long userId = AuthUtil.getUserIdFromAuth();
        AddressResponse response = addressService.updateAddress(userId, addressId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable("id") Long addressId) {
        Long userId = AuthUtil.getUserIdFromAuth();
        addressService.deleteAddress(userId, addressId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/default")
    public ResponseEntity<AddressResponse> setDefaultAddress(@PathVariable("id") Long addressId) {
        Long userId = AuthUtil.getUserIdFromAuth();
        AddressResponse response = addressService.setDefaultAddress(userId, addressId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/default")
    public ResponseEntity<AddressResponse> getDefaultAddress() {
        Long userId = AuthUtil.getUserIdFromAuth();
        AddressResponse response = addressService.getDefaultAddress(userId);
        return ResponseEntity.ok(response);
    }
}
