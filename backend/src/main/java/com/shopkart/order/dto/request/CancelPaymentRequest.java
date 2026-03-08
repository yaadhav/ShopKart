package com.shopkart.order.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CancelPaymentRequest {
    private Long paymentIntentId;
}
