package com.shopkart.order.model;

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

import java.math.BigDecimal;

@Entity
@Table(name = "ordermapping")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderMappingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ordermappingid")
    private Long orderMappingId;

    @Column(name = "orderid", nullable = false)
    private Long orderId;

    @Column(name = "userid", nullable = false)
    private Long userId;

    @Column(name = "productid", nullable = false)
    private Long productId;

    @Column(name = "imageurl", nullable = false)
    private String imageUrl;

    @Column(name = "productname", nullable = false)
    private String productName;

    @Column(name = "size", nullable = false)
    private Integer size;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "sellingprice", nullable = false, precision = 12, scale = 2)
    private BigDecimal sellingPrice;

    @Column(name = "originalprice", nullable = false, precision = 12, scale = 2)
    private BigDecimal originalPrice;

    @Column(name = "savings", nullable = false, precision = 12, scale = 2)
    private BigDecimal savings;

    @Column(name = "createdtime", nullable = false, updatable = false)
    private Long createdTime;

    @PrePersist
    protected void onCreate() {
        this.createdTime = System.currentTimeMillis();
    }
}
