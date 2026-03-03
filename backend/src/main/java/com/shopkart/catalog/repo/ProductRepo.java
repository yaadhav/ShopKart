package com.shopkart.catalog.repo;

import com.shopkart.catalog.dto.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo extends JpaRepository<ProductEntity, Long>, JpaSpecificationExecutor<ProductEntity> {
}

