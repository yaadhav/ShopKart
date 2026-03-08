package com.shopkart.order.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orderaddress")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderAddressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderaddressid")
    private Long orderAddressId;

    @Column(name = "orderid", nullable = false, unique = true)
    private Long orderId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "contactnumber", nullable = false)
    private String contactNumber;

    @Column(name = "firstline", nullable = false)
    private String firstLine;

    @Column(name = "secondline", nullable = false)
    private String secondLine;

    @Column(name = "landmark")
    private String landmark;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "pincode", nullable = false)
    private String pincode;

    @Column(name = "createdtime", nullable = false, updatable = false)
    private Long createdTime;

    @PrePersist
    protected void onCreate() {
        this.createdTime = System.currentTimeMillis();
    }
}
