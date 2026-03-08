package com.shopkart.cart.controller;

import com.shopkart.cart.dto.request.AddToCartRequest;
import com.shopkart.cart.dto.request.UpdateQuantityRequest;
import com.shopkart.cart.service.CartService;
import com.shopkart.catalog.dto.enums.Size;
import com.shopkart.user.internalagent.UserAgent;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/cart")
public class CartAPI {

    private final CartService cartService;
    private final UserAgent userAgent;

    public CartAPI(CartService cartService, UserAgent userAgent) {
        this.cartService = cartService;
        this.userAgent = userAgent;
    }

    @GetMapping
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<Map<String, Object>> getCart() {
        Long userId = userAgent.getCurrentUserId();
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<Map<String, Object>> addToCart(@RequestBody AddToCartRequest request) {
        Long userId = userAgent.getCurrentUserId();
        Integer sizeCode = Size.getCode(request.getSize());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cartService.addToCart(userId, request.getProductId(), sizeCode));
    }

    @PatchMapping("/{cartId}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<Map<String, Object>> updateQuantity(@PathVariable Long cartId, @Valid @RequestBody UpdateQuantityRequest request) {
        Long userId = userAgent.getCurrentUserId();
        return ResponseEntity.ok(cartService.updateQuantity(userId, cartId, request.getQuantity()));
    }

    @DeleteMapping("/{cartId}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long cartId) {
        Long userId = userAgent.getCurrentUserId();
        cartService.removeFromCart(userId, cartId);
        return ResponseEntity.noContent().build();
    }
}
