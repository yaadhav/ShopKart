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
@Table(name = "feedetails")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeDetailsEntity extends PrePersistenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedetailsid")
    private Long feeDetailsId;

    @Column(name = "deliveryfee", nullable = false, precision = 10, scale = 2)
    private BigDecimal deliveryFee;

    @Column(name = "platformfee", nullable = false, precision = 10, scale = 2)
    private BigDecimal platformFee;

    @Column(name = "updatedby", nullable = false)
    private Long updatedBy;
}
