package com.shopkart.order.internalagent.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
@AllArgsConstructor
@Builder
public class FeeResource {
    private Long feeDetailsId;
    private BigDecimal deliveryFee;
    private BigDecimal platformFee;
}
