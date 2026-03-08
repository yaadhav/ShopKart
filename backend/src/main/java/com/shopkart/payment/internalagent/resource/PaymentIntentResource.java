package com.shopkart.payment.internalagent.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
@AllArgsConstructor
@Builder
public class PaymentIntentResource {
    private Long paymentIntentId;
    private Long userId;
    private Long orderId;
    private BigDecimal totalAmount;
    private Integer paymentStatus;
    private Long createdTime;
}
