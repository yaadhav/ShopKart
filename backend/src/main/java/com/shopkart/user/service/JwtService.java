package com.shopkart.user.service;

import com.shopkart.common.util.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final long expirationMs;

    public JwtService(JwtEncoder jwtEncoder,
                      @Value("${shopkart.jwt.expiration-ms:3600000}") long expirationMs) {
        this.jwtEncoder = jwtEncoder;
        this.expirationMs = expirationMs;
    }

    public String generateToken(Long userId, String email, String role) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(Constants.Jwt.ISSUER)
                .issuedAt(now)
                .expiresAt(now.plusMillis(expirationMs))
                .subject(String.valueOf(userId))
                .claim(Constants.Jwt.CLAIM_EMAIL, email)
                .claim(Constants.Jwt.CLAIM_ROLE, role)
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
