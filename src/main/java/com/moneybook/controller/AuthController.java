package com.moneybook.controller;

import com.moneybook.dto.api.ApiResponse;
import com.moneybook.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("${api.base-path}/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getUserDetails(@CookieValue(name = "auth-token", required = false) String accessToken, HttpServletResponse response) {
        if (accessToken == null || !jwtUtil.validateToken(accessToken)) {
            log.error("Invalid access token from {}", response.getHeader("Referer")); // Log the origin of the request
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.builder() // Signals frontend to refresh token
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("User is not authenticated")
                    .build());
        }

        String userId = jwtUtil.extractUserId(accessToken);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("User is authenticated")
                .data(Map.of("userId", userId))
                .build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@CookieValue(name = "refresh-token", required = false) String refreshToken, HttpServletResponse response) {
        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
            log.error("Invalid refresh token from :{}", response.getHeader("Referer")); // Log the origin of the request
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userId = jwtUtil.extractUserId(refreshToken);
        String newAccessToken = jwtUtil.generateAccessToken(userId);

        JwtUtil.setAccessTokenCookie(response, newAccessToken); // Set new access token in HTTP-only cookie
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Token refreshed")
                .build());
    }

}
