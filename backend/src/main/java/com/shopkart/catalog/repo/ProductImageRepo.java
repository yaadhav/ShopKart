package com.shopkart.catalog.repo;

import com.shopkart.catalog.model.ProductImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImageRepo extends JpaRepository<ProductImageEntity, Long> {
    List<ProductImageEntity> findByProductIdOrderByDisplayOrder(Long productId);
    Optional<ProductImageEntity> findByProductIdAndIsThumbnail(Long productId, Boolean isThumbnail);
}
