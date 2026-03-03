package com.shopkart.common.exception;

import org.springframework.http.HttpStatus;

public interface ExceptionStore {

    HttpStatus getStatus();

    String getErrorCode();

    String getMessage();

    default ShopKartException exception() {
        return new ShopKartException(getStatus(), getErrorCode(), getMessage());
    }
}
