package com.shopkart.common.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ErrorResponse {

    private final int status;
    private final String errorCode;
    private final String message;
}
