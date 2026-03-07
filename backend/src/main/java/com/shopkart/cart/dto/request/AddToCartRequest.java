package com.shopkart.cart.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddToCartRequest {

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("size")
    private String size;

    public Long getProductId() { return productId; }
    public String getSize() { return size; }
}
