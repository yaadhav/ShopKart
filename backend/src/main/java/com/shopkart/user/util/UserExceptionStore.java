package com.shopkart.user.util;

import com.shopkart.common.exception.ExceptionStore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserExceptionStore implements ExceptionStore {

    USER_DETAILS_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_001", "User details not found"),
    ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_002", "Address not found"),
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "USER_003", "You are not authorized to access this resource"),
    INVALID_PHONE_NUMBER(HttpStatus.BAD_REQUEST, "USER_004", "Invalid phone number format"),
    INVALID_PINCODE(HttpStatus.BAD_REQUEST, "USER_005", "Invalid pincode format");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
