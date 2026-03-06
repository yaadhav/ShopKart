package com.shopkart.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CreateAdminResponse {

    private String email;
    private String name;
    private String role;
    private String generatedPassword;
}
