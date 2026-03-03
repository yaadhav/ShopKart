package com.shopkart.catalog.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
@AllArgsConstructor
@Builder
public class ProductDTO {
    private Long productId;
    private String name;
    private String description;
    private BigDecimal sellingPrice;
    private BigDecimal originalPrice;
    private Integer discountPercentage;
    private BigDecimal rating;
    private String brand;
    private String fashionStyle;
    private String category;
    private String occasion;
    private String size;
    private String image;
    private Integer stock;
}
