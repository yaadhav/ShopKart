package com.shopkart.cart.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "wishlist")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wishlistid")
    private Long wishlistId;

    @Column(name = "userid", nullable = false)
    private Long userId;

    @Column(name = "productid", nullable = false)
    private Long productId;

    @Column(name = "createdtime", nullable = false, updatable = false)
    private Long createdTime;

    @PrePersist
    protected void onCreate() {
        this.createdTime = System.currentTimeMillis();
    }
}
