package com.shopkart.payment.repo;

import com.shopkart.payment.model.PaymentIntentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentIntentRepo extends JpaRepository<PaymentIntentEntity, Long> {
    Optional<PaymentIntentEntity> findByPaymentIntentId(Long paymentIntentId);
    Optional<PaymentIntentEntity> findByOrderId(Long orderId);
}
