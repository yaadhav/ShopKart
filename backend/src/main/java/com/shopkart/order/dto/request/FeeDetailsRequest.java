package com.shopkart.order.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeeDetailsRequest {
    private BigDecimal deliveryFee;
    private BigDecimal platformFee;
}
