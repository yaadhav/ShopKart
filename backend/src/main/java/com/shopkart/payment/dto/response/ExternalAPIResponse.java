package com.shopkart.payment.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class ExternalAPIResponse {
    private int code;
    private String status;
    private String message;
    private boolean isSuccess;
}
