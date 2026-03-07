package com.shopkart.order.repo;

import com.shopkart.order.model.FeeDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeeDetailsRepo extends JpaRepository<FeeDetailsEntity, Long> {
    Optional<FeeDetailsEntity> findTopByOrderByFeeDetailsIdDesc();
}
