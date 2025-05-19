package com.bugsquashers.backend.auth.jwt;


import com.bugsquashers.backend.user.domain.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Getter
public class JwtGenerator {
    private final SecretKey AccessKey;
    private final SecretKey RefreshKey;
    private final long accessTokenExpTime;
    private final long RefreshTokenExpTime;

    public JwtGenerator(
            @Value("${jwt.secretAccessKey}") String secretAccessKey,
            @Value("${jwt.secretRefreshKey}") String secretRefreshKey,
            @Value("${jwt.accessExpTime}") long accessTokenExpTime,
            @Value("${jwt.refreshExpTime}") long RefreshTokenExpTime
    ) {
        this.AccessKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretAccessKey));
        this.RefreshKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretRefreshKey));
        this.accessTokenExpTime = accessTokenExpTime;
        this.RefreshTokenExpTime = RefreshTokenExpTime;
    }

    //AccessToken 생성
    public String generateAccessToken(User user) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .subject(user.getUsername())
                .claims(createClaims(user))
                .expiration(new Date(now + accessTokenExpTime))
                .signWith(AccessKey)
                .compact();
    }

    //RefreshToken 생성
    public String generateRefreshToken(User user) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .subject(user.getUsername())
                .claims(createClaims(user))
                .expiration(new Date(now + RefreshTokenExpTime))
                .signWith(RefreshKey)
                .compact();
    }

    private Map<String, Object> createClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("Identifier", user.getUserId());
        claims.put("isAdmin", user.getIsAdmin());
        return claims;
    }

    public ResponseCookie createTokenCookie(String prefix, String token) {
        long maxAgeSec;
        if ("access_token".equals(prefix)) {
            maxAgeSec = accessTokenExpTime / 1000;
        } else if ("refresh_token".equals(prefix)) {
            maxAgeSec = RefreshTokenExpTime / 1000;
        } else {
            throw new IllegalArgumentException("잘못된 prefix: " + prefix);
        }

        return ResponseCookie.from(prefix, token)
                .path("/")
                .maxAge(maxAgeSec)
                .httpOnly(true)
                .sameSite("Lax")
                .secure(false)
                .build();
    }

    public LocalDateTime getRefreshTokenExpTime() {
        return LocalDateTime.now().plusSeconds(RefreshTokenExpTime / 1000);
    }

}
