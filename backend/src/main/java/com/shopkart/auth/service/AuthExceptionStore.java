package com.shopkart.auth.service;

import com.shopkart.common.exception.ExceptionStore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthExceptionStore implements ExceptionStore {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH_001", "User not found"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH_002", "Invalid email or password"),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "AUTH_003", "User with this email already exists"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH_004", "Unauthorized access"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH_005", "Token has expired"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH_006", "Access denied");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
