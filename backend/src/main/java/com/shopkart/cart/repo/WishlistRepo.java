package com.shopkart.cart.repo;

import com.shopkart.cart.model.WishlistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishlistRepo extends JpaRepository<WishlistEntity, Long> {
    List<WishlistEntity> findByUserId(Long userId);
    boolean existsByUserIdAndProductId(Long userId, Long productId);
}
