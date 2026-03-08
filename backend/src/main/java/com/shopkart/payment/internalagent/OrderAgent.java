package com.shopkart.payment.internalagent;

import com.shopkart.order.model.OrderEntity;
import com.shopkart.order.model.OrderMappingEntity;
import com.shopkart.order.repo.OrderMappingRepo;
import com.shopkart.order.repo.OrderRepo;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class OrderAgent {

    private final OrderRepo orderRepo;
    private final OrderMappingRepo orderMappingRepo;

    public OrderAgent(OrderRepo orderRepo, OrderMappingRepo orderMappingRepo) {
        this.orderRepo = orderRepo;
        this.orderMappingRepo = orderMappingRepo;
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

    public Map<Long, Map<Integer, Integer>> getProductQuantityDetails(Long orderId) {
        List<OrderMappingEntity> mappings = orderMappingRepo.findByOrderId(orderId);

        Map<Long, Map<Integer, Integer>> productQuantityMap = new HashMap<>();
        for(OrderMappingEntity mapping : mappings) {
            productQuantityMap
                    .computeIfAbsent(mapping.getProductId(), k -> new HashMap<>())
                    .merge(mapping.getSize(), mapping.getQuantity(), Integer::sum);
        }
        return productQuantityMap;
    }

    public BigDecimal getOrderTotal(Long orderId) {
        return orderRepo.findById(orderId)
                .map(OrderEntity::getOrderTotal)
                .orElse(BigDecimal.ZERO);
    }
}
