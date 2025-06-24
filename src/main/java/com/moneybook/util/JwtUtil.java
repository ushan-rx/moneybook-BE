package com.moneybook.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.access-secret-key}")
    private String secretKey;

    @Value("${jwt.refresh-secret-key}")
    private String refreshSecretKey;

    private static final long ACCESS_TOKEN_EXPIRY = 15 * 60 * 1000;  // 15 minutes
    private static final long REFRESH_TOKEN_EXPIRY = 7 * 24 * 60 * 60 * 1000;  // 7 days

    private SecretKey getAccessSecret() {
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private SecretKey getRefreshSecret() {
        byte[] keyBytes = refreshSecretKey.getBytes();
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
//        accessCookie.setSecure(true); // Ensures cookie is only sent over HTTPS
        accessCookie.setPath("/");
        accessCookie.setMaxAge((int) (ACCESS_TOKEN_EXPIRY / 1000));
        accessCookie.setAttribute("SameSite", "Strict");

        Cookie refreshCookie = new Cookie("refresh-token", refreshToken);
        refreshCookie.setHttpOnly(true);
//        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge((int) (REFRESH_TOKEN_EXPIRY / 1000));
        refreshCookie.setAttribute("SameSite", "Strict");

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
    }

    public static void setAccessTokenCookie(HttpServletResponse response, String accessToken) {
        Cookie accessCookie = new Cookie("auth-token", accessToken);
        accessCookie.setHttpOnly(true);
//        accessCookie.setSecure(true); // Ensures cookie is only sent over HTTPS
        accessCookie.setPath("/");
        accessCookie.setMaxAge((int) (ACCESS_TOKEN_EXPIRY / 1000));
        accessCookie.setAttribute("SameSite", "Strict");

        response.addCookie(accessCookie);
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

    public boolean validateAccessToken(String accessToken) {
        try {
            Jwts.parser()
                    .verifyWith(getAccessSecret()) // verify signature
                    .build()
                    .parseSignedClaims(accessToken);
            return true;
        }catch (Exception e){
            log.error("Error parsing token: {}", e.getMessage());
            return false;
        }
    }

    public boolean validateRefreshToken(String refreshToken) {
        try {
            Jwts.parser()
                    .verifyWith(getRefreshSecret()) // verify signature
                    .build()
                    .parseSignedClaims(refreshToken);
            return true;
        }catch (Exception e){
            log.error("Error parsing token: {}", e.getMessage());
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

    public String extractUserIdFromRefreshToken(String token){
        return Jwts.parser()
                .verifyWith(getRefreshSecret())
                .build()
                .parseSignedClaims(token) // parse token
                .getPayload()
                .getSubject();  // get userId from payload
    }


}
