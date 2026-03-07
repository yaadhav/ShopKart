package com.shopkart.cart.model;

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
@Table(name = "cart")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartEntity extends PrePersistenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cartid")
    private Long cartId;

    @Column(name = "userid", nullable = false)
    private Long userId;

    @Column(name = "productid", nullable = false)
    private Long productId;

    @Column(name = "size", nullable = false)
    private Integer size;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;
}
