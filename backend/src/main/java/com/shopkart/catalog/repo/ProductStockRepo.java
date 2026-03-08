package com.shopkart.catalog.repo;

import com.shopkart.catalog.model.ProductStockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductStockRepo extends JpaRepository<ProductStockEntity, Long> {
    List<ProductStockEntity> findByProductId(Long productId);
    List<ProductStockEntity> findByProductIdOrderBySize(Long productId);
    Optional<ProductStockEntity> findByProductIdAndSize(Long productId, Integer size);

    @Query("SELECT DISTINCT ps.productId FROM ProductStockEntity ps WHERE ps.quantity = 0")
    List<Long> findProductIdsWithNoStock();
}
