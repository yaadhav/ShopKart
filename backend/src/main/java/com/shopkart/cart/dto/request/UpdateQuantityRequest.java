package com.shopkart.cart.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.Getter;

@Getter
public class UpdateQuantityRequest {

    @JsonProperty("quantity")
    @Min(1)
    private Integer quantity;
}
