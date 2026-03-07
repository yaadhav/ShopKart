package com.shopkart.catalog.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private Long productId;
    private String name;
    private String tagline;
    private BigDecimal sellingPrice;
    private BigDecimal originalPrice;
    private Integer discountPercentage;
    private BigDecimal rating;
    private Integer ratingCount;
    private String brand;
    private String fashionStyle;
    private String category;
    private String occasion;
    private String image;
}
