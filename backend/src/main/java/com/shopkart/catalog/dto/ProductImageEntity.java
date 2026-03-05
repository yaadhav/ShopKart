package com.shopkart.catalog.dto;

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

@Entity
@Table(name = "productimages")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "productimagesid")
    private Long productImagesId;

    @Column(name = "productid", nullable = false)
    private Long productId;

    @Column(name = "imageurl", length = 512, nullable = false)
    private String imageUrl;

    @lombok.Builder.Default
    @Column(name = "isthumbnail")
    private Boolean isThumbnail = false;

    @Column(name = "displayorder", nullable = false)
    private Integer displayOrder;

    @Column(name = "createdtime", nullable = false)
    private Long createdTime;
}
