package com.shopkart.user.repo;

import com.shopkart.user.model.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepo extends JpaRepository<AddressEntity, Long> {

    List<AddressEntity> findByUserId(Long userId);

    Optional<AddressEntity> findByAddressIdAndUserId(Long addressId, Long userId);

    Optional<AddressEntity> findByUserIdAndIsDefaultTrue(Long userId);

    @Modifying
    @Query("UPDATE AddressEntity a SET a.isDefault = false WHERE a.userId = :userId AND a.isDefault = true")
    void unsetDefaultAddressForUser(@Param("userId") Long userId);
}
