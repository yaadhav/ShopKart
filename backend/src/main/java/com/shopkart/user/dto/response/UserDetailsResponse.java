package com.shopkart.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetailsResponse {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("gender")
    private Short gender;

    @JsonProperty("date_of_birth")
    private LocalDate dateOfBirth;

    @JsonProperty("created_time")
    private Long createdTime;

    @JsonProperty("updated_time")
    private Long updatedTime;
}
