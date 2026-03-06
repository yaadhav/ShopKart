package com.shopkart.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponse {

    @JsonProperty("address_id")
    private Long addressId;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("contact_number")
    private String contactNumber;

    @JsonProperty("first_line")
    private String firstLine;

    @JsonProperty("second_line")
    private String secondLine;

    @JsonProperty("landmark")
    private String landmark;

    @JsonProperty("city")
    private String city;

    @JsonProperty("state")
    private String state;

    @JsonProperty("pincode")
    private String pincode;

    @JsonProperty("is_default")
    private Boolean isDefault;

    @JsonProperty("created_time")
    private Long createdTime;

    @JsonProperty("updated_time")
    private Long updatedTime;
}
