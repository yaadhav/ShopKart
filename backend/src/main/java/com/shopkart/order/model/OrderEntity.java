package com.shopkart.order.model;

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
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEntity extends PrePersistenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderid")
    private Long orderId;

    @Column(name = "userid", nullable = false)
    private Long userId;

    @Column(name = "paymentid")
    private Long paymentId;

    @Column(name = "orderamount", nullable = false, precision = 12, scale = 2)
    private BigDecimal orderAmount;

    @Column(name = "ordersavings", nullable = false, precision = 12, scale = 2)
    private BigDecimal orderSavings;

    @Column(name = "conveniencefee", nullable = false, precision = 12, scale = 2)
    private BigDecimal convenienceFee;

    @Column(name = "ordertotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal orderTotal;

    @Column(name = "paymentmode", nullable = false)
    private Integer paymentMode;

    @Column(name = "paymentstatus", nullable = false)
    private Integer paymentStatus;

    @Column(name = "orderstatus", nullable = false)
    private Integer orderStatus;

    @Column(name = "initiatedtime", nullable = false)
    private Long initiatedTime;

    @Column(name = "deliveredtime")
    private Long deliveredTime;
}
