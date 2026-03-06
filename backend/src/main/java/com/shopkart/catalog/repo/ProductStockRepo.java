package com.shopkart.catalog.repo;

import com.shopkart.catalog.model.ProductStockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductStockRepo extends JpaRepository<ProductStockEntity, Long> {
    List<ProductStockEntity> findByProductId(Long productId);
    Optional<ProductStockEntity> findByProductIdAndSize(Long productId, Integer size);
    void deleteByProductId(Long productId);
}
