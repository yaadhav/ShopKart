package com.shopkart.payment.util;

import com.shopkart.common.exception.ExceptionStore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PaymentExceptionStore implements ExceptionStore {
    PAYMENT_INTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT_001", "Payment intent not found"),
    INVALID_SECRET_KEY(HttpStatus.FORBIDDEN, "PAYMENT_002", "Invalid secret key"),
    PAYMENT_EXPIRED(HttpStatus.BAD_REQUEST, "PAYMENT_003", "Payment session expired"),
    AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "PAYMENT_004", "Payment amount does not match"),
    PAYMENT_ALREADY_PROCESSED(HttpStatus.CONFLICT, "PAYMENT_005", "Payment already processed"),
    PAYMENT_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "PAYMENT_006", "Payment Creation Failed");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
