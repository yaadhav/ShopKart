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

@Entity
@Table(name = "productstock")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductStockEntity extends PrePersistenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "productstockid")
    private Long productStockId;

    @Column(name = "productid", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer size;

    @lombok.Builder.Default
    @Column(nullable = false)
    private Integer quantity = 0;
}
