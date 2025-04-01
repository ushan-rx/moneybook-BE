package com.moneybook.controller;

import com.moneybook.dto.api.ApiResponse;
import com.moneybook.dto.user.NormalUserBriefDto;
import com.moneybook.exception.ResourceNotFoundException;
import com.moneybook.service.NormalUserService;
import com.moneybook.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("${api.base-path}/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final NormalUserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> getUserDetails(
            @CookieValue(name = "auth-token", required = false) String accessToken,
            HttpServletResponse response) throws ResourceNotFoundException {

        if (accessToken == null || !jwtUtil.validateAccessToken(accessToken)) {
            log.error("Invalid access token from {}", response.getHeader("Referer")); // Log the origin of the request
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.builder() // Signals frontend to refresh token
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("User is not authenticated")
                    .build());
        }

        String userId = jwtUtil.extractUserId(accessToken);
        NormalUserBriefDto userDetails = userService.getUserBrief(userId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("User is authenticated")
                .data(userDetails)
                .build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@CookieValue(name = "refresh-token", required = false) String refreshToken, HttpServletResponse response) {
        if (refreshToken == null || !jwtUtil.validateRefreshToken(refreshToken)) {
            log.error("Invalid refresh token from :{}", response.getHeader("Referer")); // Log the origin of the request
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userId = jwtUtil.extractUserIdFromRefreshToken(refreshToken);
        String newAccessToken = jwtUtil.generateAccessToken(userId);

        JwtUtil.setAccessTokenCookie(response, newAccessToken); // Set new access token in HTTP-only cookie
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Token refreshed")
                .build());
    }

}
