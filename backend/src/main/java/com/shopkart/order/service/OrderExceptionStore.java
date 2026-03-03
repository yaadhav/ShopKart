package com.shopkart.order.service;

import com.shopkart.common.exception.ExceptionStore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum OrderExceptionStore implements ExceptionStore {
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
