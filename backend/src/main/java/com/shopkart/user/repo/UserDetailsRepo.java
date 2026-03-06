package com.shopkart.user.repo;

import com.shopkart.user.model.UserDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDetailsRepo extends JpaRepository<UserDetailsEntity, Long> {

    Optional<UserDetailsEntity> findByUserId(Long userId);
}
