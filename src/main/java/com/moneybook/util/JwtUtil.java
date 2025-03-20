package com.moneybook.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.access-secret-key}")
    private String secretKey;

    @Value("${jwt.refresh-secret-key}")
    private String refreshSecretKey;

    private static final long ACCESS_TOKEN_EXPIRY = 15 * 60 * 1000;  // 15 minutes
    private static final long REFRESH_TOKEN_EXPIRY = 7 * 24 * 60 * 60 * 1000;  // 7 days

    private SecretKey getAccessSecret() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private SecretKey getRefreshSecret() {
        byte[] keyBytes = Base64.getDecoder().decode(refreshSecretKey); // Decode Base64
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(String subId) {
        return Jwts.builder()
                .subject(subId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRY))
                .signWith(getAccessSecret())
                .compact();
    }

    public String generateRefreshToken(String subId) {
        return Jwts.builder()
                .subject(subId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRY))
                .signWith(getRefreshSecret())
                .compact();
    }

    public static void setJwtCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        Cookie accessCookie = new Cookie("auth-token", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true); // Ensures cookie is only sent over HTTPS
        accessCookie.setPath("/");
        accessCookie.setMaxAge((int) (ACCESS_TOKEN_EXPIRY / 1000));
        accessCookie.setAttribute("SameSite", "Strict");

        Cookie refreshCookie = new Cookie("refresh-token", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge((int) (REFRESH_TOKEN_EXPIRY / 1000));
        refreshCookie.setAttribute("SameSite", "Strict");

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
    }

    public static void clearCookies(HttpServletResponse response) {
        Cookie accessCookie = new Cookie("auth-token", null);
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(0);

        Cookie refreshCookie = new Cookie("refresh-token", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getAccessSecret()) // verify signature
                    .build()
                    .parseSignedClaims(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public String extractUserId(String token){
        return Jwts.parser()
                .verifyWith(getAccessSecret())
                .build()
                .parseSignedClaims(token) // parse token
                .getPayload()
                .getSubject();  // get userId from payload
    }
}
