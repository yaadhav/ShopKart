package com.shopkart.catalog.dto;

import com.shopkart.common.config.PrePersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductEntity extends PrePersistenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "productid")
    private Long productId;

    @Column(nullable = false)
    private String name;

    @Column(length = 50)
    private String description;

    @Column(name = "sellingprice", nullable = false, precision = 19, scale = 2)
    private BigDecimal sellingPrice;

    @Column(name = "originalprice", nullable = false, precision = 19, scale = 2)
    private BigDecimal originalPrice;

    @Column(name = "discountpercentage")
    private Integer discountPercentage;

    @Column(precision = 2, scale = 1)
    private BigDecimal rating;

    @Column(name = "ratingcount", nullable = false)
    private Integer ratingCount;

    @Column(name = "brand")
    private Integer brand;

    @Column(name = "fashionstyle")
    private Integer fashionStyle;

    @Column(name = "category")
    private Integer category;

    @Column(name = "occasion")
    private Integer occasion;

    @Column(name = "size")
    private Integer size;

    private String image;
}


