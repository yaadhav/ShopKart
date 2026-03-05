package com.shopkart.catalog.repository;

import com.shopkart.catalog.dto.ProductDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductDetailsRepo extends JpaRepository<ProductDetailsEntity, Long> {
    Optional<ProductDetailsEntity> findByProductId(Long productId);
    void deleteByProductId(Long productId);
}
