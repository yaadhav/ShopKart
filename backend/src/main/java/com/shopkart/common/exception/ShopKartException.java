package com.shopkart.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ShopKartException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    public ShopKartException(HttpStatus status, String errorCode, String message) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }
}
