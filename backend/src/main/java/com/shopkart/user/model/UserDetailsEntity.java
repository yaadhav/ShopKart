package com.shopkart.user.model;

import com.shopkart.common.config.PrePersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "userdetails")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetailsEntity extends PrePersistenceEntity {

    @Id
    @Column(name = "userid")
    private Long userId;

    @Column(name = "phonenumber", length = 15)
    private String phoneNumber;

    @Column(name = "gender")
    private Integer gender;

    @Column(name = "dateofbirth")
    private LocalDate dateOfBirth;
}
