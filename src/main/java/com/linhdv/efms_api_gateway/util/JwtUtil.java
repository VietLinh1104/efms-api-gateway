package com.linhdv.efms_api_gateway.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders; // Thêm import này
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtUtil {

    @Value("${application.security.jwt.secret-key}")
    private String secret;

    private Key getSigningKey() {
        // ĐỒNG BỘ VỚI IDENTITY SERVICE: Bắt buộc dùng Decoders.BASE64.decode
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public void validateToken(final String token) {
        Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
    }
}