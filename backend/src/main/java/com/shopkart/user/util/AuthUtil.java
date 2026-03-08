package com.shopkart.user.util;

import com.shopkart.common.util.Constants;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public class AuthUtil {
    public static Long getUserIdFromJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("No valid authentication found");
        }
        return Long.parseLong(jwt.getSubject());
    }

    public static String getRoleFromJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("No valid authentication found");
        }
        return jwt.getClaimAsString(Constants.Jwt.CLAIM_ROLE);
    }
}
