package com.shopkart.cart.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class AddToCartRequest {

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("size")
    private String size;
}
