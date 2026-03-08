package com.shopkart.payment.internalagent;

import com.shopkart.order.dto.enums.PaymentStatus;
import com.shopkart.payment.dto.enums.PaymentMethod;
import com.shopkart.payment.internalagent.resource.PaymentIntentResource;
import com.shopkart.payment.model.PaymentIntentEntity;
import com.shopkart.payment.repo.PaymentIntentRepo;
import com.shopkart.payment.repo.PaymentRepo;
import com.shopkart.payment.util.PaymentConstants;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class PaymentAgent {

    private final PaymentIntentRepo paymentIntentRepo;
    private final PaymentRepo paymentRepo;

    public PaymentAgent(PaymentIntentRepo paymentIntentRepo, PaymentRepo paymentRepo) {
        this.paymentIntentRepo = paymentIntentRepo;
        this.paymentRepo = paymentRepo;
    }

    @Transactional
    public Long createPaymentIntent(Long userId, Long orderId, BigDecimal totalAmount) {
        PaymentIntentEntity entity = PaymentIntentEntity.builder()
                .userId(userId)
                .orderId(orderId)
                .totalAmount(totalAmount)
                .paymentStatus(PaymentStatus.INITIATED.code)
                .build();
        return paymentIntentRepo.save(entity).getPaymentIntentId();
    }

    public PaymentIntentResource getPaymentIntent(Long paymentIntentId) {
        return paymentIntentRepo.findById(paymentIntentId)
                .map(this::toResource)
                .orElse(null);
    }

    @Transactional
    public void updatePaymentIntentStatus(Long paymentIntentId, Integer status) {
        paymentIntentRepo.findById(paymentIntentId).ifPresent(entity -> {
            entity.setPaymentStatus(status);
            paymentIntentRepo.save(entity);
        });
    }

    public Map<String, Object> getPaymentByOrderId(Long orderId) {
        return paymentRepo.findByOrderId(orderId)
                .map(entity -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put(PaymentConstants.Keys.PAYMENT_METHOD, PaymentMethod.getName(entity.getPaymentMethod()));
                    map.put(PaymentConstants.Keys.PAYMENT_METHOD + PaymentConstants.Keys.FORMATTED_SUFFIX,
                            PaymentMethod.getDisplayName(entity.getPaymentMethod()));
                    map.put(PaymentConstants.Keys.REFERENCE_ID, entity.getReferenceId());
                    return map;
                })
                .orElse(null);
    }

    private PaymentIntentResource toResource(PaymentIntentEntity entity) {
        return PaymentIntentResource.builder()
                .paymentIntentId(entity.getPaymentIntentId())
                .userId(entity.getUserId())
                .orderId(entity.getOrderId())
                .totalAmount(entity.getTotalAmount())
                .paymentStatus(entity.getPaymentStatus())
                .createdTime(entity.getCreatedTime())
                .build();
    }
}
