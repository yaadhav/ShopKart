package com.shopkart.payment.model;

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
@Table(name = "payment")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEntity extends PrePersistenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paymentid")
    private Long paymentId;

    @Column(name = "userid", nullable = false)
    private Long userId;

    @Column(name = "orderid", nullable = false)
    private Long orderId;

    @Column(name = "paymentintentid", nullable = false)
    private Long paymentIntentId;

    @Column(name = "paymentmethod", nullable = false)
    private Integer paymentMethod;

    @Column(name = "paymentmode", nullable = false)
    private Integer paymentMode;

    @Column(name = "totalamount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "referenceid")
    private String referenceId;
}
