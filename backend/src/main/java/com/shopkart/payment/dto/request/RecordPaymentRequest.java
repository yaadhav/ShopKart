package com.shopkart.payment.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RecordPaymentRequest {
    private String secretKey;
    private Long paymentIntentId;
    private String paymentMethod;
    private BigDecimal totalAmount;
    private String referenceId;
    private String status;
}
