package com.shopkart.user.model;

import com.shopkart.common.config.PrePersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "address")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressEntity extends PrePersistenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "addressid")
    private Long addressId;

    @Column(name = "userid", nullable = false)
    private Long userId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "contactnumber", nullable = false, length = 15)
    private String contactNumber;

    @Column(name = "firstline", nullable = false, length = 255)
    private String firstLine;

    @Column(name = "secondline", nullable = false, length = 255)
    private String secondLine;

    @Column(name = "landmark", length = 255)
    private String landmark;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "state", nullable = false, length = 100)
    private String state;

    @Column(name = "pincode", nullable = false, length = 10)
    private String pincode;

    @Column(name = "isdefault", nullable = false)
    private Boolean isDefault;
}
