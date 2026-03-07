package com.shopkart.cart.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddToWishlistRequest {

    @JsonProperty("product_id")
    private Long productId;

    public Long getProductId() { return productId; }
}
