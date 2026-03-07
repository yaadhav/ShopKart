package com.shopkart.cart.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;

public class UpdateQuantityRequest {

    @JsonProperty("quantity")
    @Min(1)
    private Integer quantity;

    public Integer getQuantity() { return quantity; }
}
