package com.shopkart.order.repo;

import com.shopkart.order.model.OrderMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderMappingRepo extends JpaRepository<OrderMappingEntity, Long> {
    List<OrderMappingEntity> findByOrderId(Long orderId);
    List<OrderMappingEntity> findByOrderIdIn(List<Long> orderIds);
}
