package com.shopkart.cart.repo;

import com.shopkart.cart.model.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepo extends JpaRepository<CartEntity, Long> {
    List<CartEntity> findByUserId(Long userId);
    Optional<CartEntity> findByUserIdAndProductIdAndSize(Long userId, Long productId, Integer size);
    void deleteByUserId(Long userId);
}
