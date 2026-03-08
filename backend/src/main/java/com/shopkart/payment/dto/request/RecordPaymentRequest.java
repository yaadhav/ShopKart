package com.shopkart.payment.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RecordPaymentRequest {
    private String secretKey;
    private Long paymentIntentId;
    private String paymentMethod;    // "upi", "credit_card", etc.
    private BigDecimal amountPaid;
    private String referenceId;
    private boolean success;         // true = payment succeeded, false = payment failed
}
