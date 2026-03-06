package com.shopkart.catalog.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailsRequest {

    private String description;

    @Size(max = 50)
    private String color;

    @Size(max = 100)
    private String material;

    @Size(max = 50)
    private String length;

    @Size(max = 50)
    private String sleeve;

    @Size(max = 50)
    private String transparency;

    private String careInstructions;
}
