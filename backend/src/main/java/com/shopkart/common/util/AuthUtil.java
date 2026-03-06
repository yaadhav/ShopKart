package com.shopkart.common.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public class AuthUtil {

    public static Long getUserIdFromAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt)) {
            throw new IllegalStateException("No valid authentication found");
        }
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return Long.parseLong(jwt.getSubject());
    }
}
