package com.pharmaops.identity.infrastructure.security;

import com.pharmaops.identity.application.port.out.JwtPort;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class JwtAdapter implements JwtPort {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-minutes:15}")
    private long expirationMinutes;

    @Override
    public String generateAccessToken(UUID userId, String email, List<String> roles, UUID storeId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + Duration.ofMinutes(expirationMinutes).toMillis());

        var builder = Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey());

        if (storeId != null) {
            builder.claim("storeId", storeId.toString());
        }

        return builder.compact();
    }

    @Override
    public Claims parseToken(String token) {
        var jwtClaims = Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        UUID userId = UUID.fromString(jwtClaims.getSubject());
        String email = jwtClaims.get("email", String.class);
        @SuppressWarnings("unchecked")
        List<String> roles = jwtClaims.get("roles", List.class);
        String storeIdStr = jwtClaims.get("storeId", String.class);
        UUID storeId = storeIdStr != null ? UUID.fromString(storeIdStr) : null;

        return new Claims(userId, email, roles != null ? roles : List.of(), storeId);
    }

    private SecretKey signingKey() {
        return io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }
}
