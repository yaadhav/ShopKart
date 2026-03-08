package com.shopkart.order.util;

import com.shopkart.common.exception.ExceptionStore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum OrderExceptionStore implements ExceptionStore {
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER_001", "Order not found"),
    EMPTY_CART(HttpStatus.BAD_REQUEST, "ORDER_002", "Cart is empty"),
    ADDRESS_NOT_FOUND(HttpStatus.BAD_REQUEST, "ORDER_003", "No delivery address found"),
    ORDER_ACCESS_DENIED(HttpStatus.FORBIDDEN, "ORDER_004", "Access denied to this order"),
    INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "ORDER_005", "Invalid order status transition");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
