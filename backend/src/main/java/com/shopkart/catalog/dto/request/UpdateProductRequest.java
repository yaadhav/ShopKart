package com.shopkart.catalog.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {

    private String name;
    private String tagline;
    private BigDecimal sellingPrice;
    private BigDecimal originalPrice;
    private Integer discountPercentage;
    private String brand;
    private String fashionStyle;
    private String category;
    private String occasion;
    private ProductDetailsRequest productDetails;
}
