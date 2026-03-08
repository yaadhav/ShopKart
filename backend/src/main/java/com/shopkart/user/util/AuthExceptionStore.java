package com.shopkart.user.util;

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
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH_005", "Access denied"),
    INVALID_ROLE(HttpStatus.BAD_REQUEST, "AUTH_006", "Invalid role specified"),
    INSUFFICIENT_PERMISSION(HttpStatus.FORBIDDEN, "AUTH_007", "Insufficient permissions to create this role");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
