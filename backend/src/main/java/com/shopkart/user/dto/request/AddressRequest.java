package com.shopkart.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequest {

    @JsonProperty("name")
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @JsonProperty("contact_number")
    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Contact number must be 10-15 digits")
    private String contactNumber;

    @JsonProperty("first_line")
    @NotBlank(message = "Address line 1 is required")
    @Size(max = 255, message = "Address line 1 must not exceed 255 characters")
    private String firstLine;

    @JsonProperty("second_line")
    @NotBlank(message = "Address line 2 is required")
    @Size(max = 255, message = "Address line 2 must not exceed 255 characters")
    private String secondLine;

    @JsonProperty("landmark")
    @Size(max = 255, message = "Landmark must not exceed 255 characters")
    private String landmark;

    @JsonProperty("city")
    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @JsonProperty("state")
    @NotBlank(message = "State is required")
    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;

    @JsonProperty("pincode")
    @NotBlank(message = "Pincode is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "Pincode must be 6 digits")
    private String pincode;

    @JsonProperty("is_default")
    private Boolean isDefault;
}
