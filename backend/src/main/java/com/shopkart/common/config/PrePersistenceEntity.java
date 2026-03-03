package com.shopkart.common.config;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter @Setter
public abstract class PrePersistenceEntity {

    @Column(name = "createdtime", nullable = false, updatable = false)
    private Long createdTime;

    @Column(name = "updatedtime", nullable = false)
    private Long updatedTime;

    @PrePersist
    protected void onCreate() {
        long now = System.currentTimeMillis();
        this.createdTime = now;
        this.updatedTime = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedTime = System.currentTimeMillis();
    }
}
