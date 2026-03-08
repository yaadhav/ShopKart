package com.shopkart.payment.internalagent;

import com.shopkart.order.model.OrderEntity;
import com.shopkart.order.repo.OrderRepo;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
public class OrderAgent {

    private final OrderRepo orderRepo;

    public OrderAgent(OrderRepo orderRepo) {
        this.orderRepo = orderRepo;
    }

    @Transactional
    public void updateOrderPayment(Long orderId, Long paymentId, Integer paymentStatus, Integer orderStatus) {
        orderRepo.findById(orderId).ifPresent(entity -> {
            entity.setPaymentId(paymentId);
            entity.setPaymentStatus(paymentStatus);
            entity.setOrderStatus(orderStatus);
            orderRepo.save(entity);
        });
    }

    public BigDecimal getOrderTotal(Long orderId) {
        return orderRepo.findById(orderId)
                .map(OrderEntity::getOrderTotal)
                .orElse(BigDecimal.ZERO);
    }
}
