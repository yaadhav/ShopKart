package com.shopkart.order.repo;

import com.shopkart.order.model.OrderAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderAddressRepo extends JpaRepository<OrderAddressEntity, Long> {
    Optional<OrderAddressEntity> findByOrderId(Long orderId);
}
