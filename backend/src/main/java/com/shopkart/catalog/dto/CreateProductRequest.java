package com.shopkart.catalog.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {

    @NotBlank
    private String name;

    @Size(max = 50)
    private String description;

    @NotNull @DecimalMin("0.01")
    private BigDecimal sellingPrice;

    @NotNull @DecimalMin("0.01")
    private BigDecimal originalPrice;

    @Min(0) @Max(100)
    private Integer discountPercentage;

    @NotBlank
    private String brand;

    @NotBlank
    private String fashionStyle;

    @NotBlank
    private String category;

    @NotBlank
    private String occasion;

    @NotBlank
    private String size;

    @Setter
    private String image;
}
