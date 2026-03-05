package com.shopkart.catalog.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {

    @NotBlank
    private String name;

    @Size(max = 100)
    private String tagline;

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

    @Valid
    private ProductDetailsRequest productDetails;

    @NotEmpty
    private Map<String, Integer> stock;

    @NotEmpty
    private List<String> images;
}
