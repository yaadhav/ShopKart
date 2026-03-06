package com.shopkart.catalog.model;

import com.shopkart.common.config.PrePersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "productdetails")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailsEntity extends PrePersistenceEntity {

    @Id
    @Column(name = "productid", nullable = false, unique = true)
    private Long productId;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50)
    private String color;

    @Column(length = 100)
    private String material;

    @Column(length = 50)
    private String length;

    @Column(length = 50)
    private String sleeve;

    @Column(length = 50)
    private String transparency;

    @Column(name = "careinstructions", columnDefinition = "TEXT")
    private String careInstructions;

    @lombok.Builder.Default
    @Column(name = "rating1star")
    private Integer rating1Star = 0;

    @lombok.Builder.Default
    @Column(name = "rating2star")
    private Integer rating2Star = 0;

    @lombok.Builder.Default
    @Column(name = "rating3star")
    private Integer rating3Star = 0;

    @lombok.Builder.Default
    @Column(name = "rating4star")
    private Integer rating4Star = 0;

    @lombok.Builder.Default
    @Column(name = "rating5star")
    private Integer rating5Star = 0;
}
