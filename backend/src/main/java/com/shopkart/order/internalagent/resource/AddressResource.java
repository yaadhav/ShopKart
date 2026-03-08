package com.shopkart.order.internalagent.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@Builder
public class AddressResource {
    private Long addressId;
    private String name;
    private String contactNumber;
    private String firstLine;
    private String secondLine;
    private String landmark;
    private String city;
    private String state;
    private String pincode;
    private Boolean isDefault;
}
