package com.shopkart.payment.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.connection.Message;

import java.math.BigDecimal;

@Getter @Setter
@Builder
public class PaymentResponse {
    private int code;
    private String status;
    private String message;
    private boolean isSuccess;
}
