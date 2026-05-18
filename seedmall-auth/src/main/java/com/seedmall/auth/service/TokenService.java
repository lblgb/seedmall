/*
 * 令牌服务，封装 JWT 签发逻辑。
 */
package com.seedmall.auth.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

/**
 * JWT 令牌服务。
 */
@Service
public class TokenService {

    private static final String DEMO_SECRET = "seedmall-demo-secret-key-for-jwt-learning-2026";

    /**
     * 签发演示 JWT。
     */
    public String issue(String username) {
        SecretKey key = Keys.hmacShaKeyFor(DEMO_SECRET.getBytes(StandardCharsets.UTF_8));
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(7200)))
                .signWith(key)
                .compact();
    }
}
