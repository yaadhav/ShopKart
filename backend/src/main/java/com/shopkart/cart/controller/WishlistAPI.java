package com.shopkart.cart.controller;

import com.shopkart.cart.dto.request.AddToWishlistRequest;
import com.shopkart.cart.service.WishlistService;
import com.shopkart.user.internalagent.UserAgent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/wishlist")
public class WishlistAPI {

    private final WishlistService wishlistService;
    private final UserAgent userAgent;

    public WishlistAPI(WishlistService wishlistService, UserAgent userAgent) {
        this.wishlistService = wishlistService;
        this.userAgent = userAgent;
    }

    @GetMapping
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<List<Map<String, Object>>> getWishlist() {
        Long userId = userAgent.getCurrentUserId();
        return ResponseEntity.ok(wishlistService.getWishlist(userId));
    }

    @PostMapping
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<Map<String, Object>> addToWishlist(@RequestBody AddToWishlistRequest request) {
        Long userId = userAgent.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(wishlistService.addToWishlist(userId, request.getProductId()));
    }

    @DeleteMapping("/{wishlistId}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<Void> removeFromWishlist(@PathVariable Long wishlistId) {
        Long userId = userAgent.getCurrentUserId();
        wishlistService.removeFromWishlist(userId, wishlistId);
        return ResponseEntity.noContent().build();
    }
}
