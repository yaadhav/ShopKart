package com.shopkart.order.repo;

import com.shopkart.order.model.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepo extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByUserId(Long userId);
    Page<OrderEntity> findByUserId(Long userId, Pageable pageable);
    Optional<OrderEntity> findByOrderIdAndUserId(Long orderId, Long userId);
    Page<OrderEntity> findByUserIdAndOrderStatusIn(Long userId, List<Integer> statuses, Pageable pageable);
}
