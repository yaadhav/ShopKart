package com.shopkart.cart.util;

import com.shopkart.common.exception.ExceptionStore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CartExceptionStore implements ExceptionStore {
    PRODUCT_NOT_IN_CART(HttpStatus.NOT_FOUND, "CART_001", "Cart item not found"),
    PRODUCT_ALREADY_IN_WISHLIST(HttpStatus.CONFLICT, "CART_002", "Product already in wishlist"),
    WISHLIST_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "CART_003", "Wishlist item not found"),
    CART_ITEM_ACCESS_DENIED(HttpStatus.FORBIDDEN, "CART_004", "Access denied to cart item"),
    WISHLIST_ITEM_ACCESS_DENIED(HttpStatus.FORBIDDEN, "CART_005", "Access denied to wishlist item");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
