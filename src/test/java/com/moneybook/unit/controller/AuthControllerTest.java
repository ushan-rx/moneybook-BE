package com.moneybook.unit.controller;

import com.moneybook.controller.AuthController;
import com.moneybook.dto.user.NormalUserBriefDto;
import com.moneybook.service.NormalUserService;
import com.moneybook.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private NormalUserService userService;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthController authController;

    private static final String VALID_TOKEN = "valid-token";
    private static final String INVALID_TOKEN = "invalid-token";
    private static final String USER_ID = "user123";

    @Test
    void getUserDetails_WithValidToken_ShouldReturnUserDetails() throws Exception {
        when(jwtUtil.validateAccessToken(VALID_TOKEN)).thenReturn(true);
        when(jwtUtil.extractUserId(VALID_TOKEN)).thenReturn(USER_ID);

        NormalUserBriefDto userDto = new NormalUserBriefDto();
        userDto.setUserId(USER_ID);
        when(userService.getUserBrief(USER_ID)).thenReturn(userDto);

        ResponseEntity<?> response = authController.getUserDetails(VALID_TOKEN, this.response);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getUserDetails_WithInvalidToken_ShouldReturnUnauthorized() throws Exception {
        when(jwtUtil.validateAccessToken(INVALID_TOKEN)).thenReturn(false);

        ResponseEntity<?> response = authController.getUserDetails(INVALID_TOKEN, this.response);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void getUserDetails_WithNullToken_ShouldReturnUnauthorized() throws Exception {
        ResponseEntity<?> response = authController.getUserDetails(null, this.response);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void validateAccessToken_WithValidToken_ShouldReturnOk() {
        when(jwtUtil.validateAccessToken(VALID_TOKEN)).thenReturn(true);

        ResponseEntity<?> response = authController.validateAccessToken(VALID_TOKEN);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void validateAccessToken_WithInvalidToken_ShouldReturnUnauthorized() {
        when(jwtUtil.validateAccessToken(INVALID_TOKEN)).thenReturn(false);

        ResponseEntity<?> response = authController.validateAccessToken(INVALID_TOKEN);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void validateAccessToken_WithNullToken_ShouldReturnUnauthorized() {
        ResponseEntity<?> response = authController.validateAccessToken(null);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void refreshAccessToken_WithValidToken_ShouldReturnNewToken() {
        when(jwtUtil.validateRefreshToken(VALID_TOKEN)).thenReturn(true);
        when(jwtUtil.extractUserIdFromRefreshToken(VALID_TOKEN)).thenReturn(USER_ID);
        String newAccessToken = "new-access-token";
        when(jwtUtil.generateAccessToken(USER_ID)).thenReturn(newAccessToken);

        ResponseEntity<?> response = authController.refreshAccessToken(VALID_TOKEN, this.response);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(jwtUtil).generateAccessToken(USER_ID);
    }

    @Test
    void refreshAccessToken_WithInvalidToken_ShouldReturnUnauthorized() {
        when(jwtUtil.validateRefreshToken(INVALID_TOKEN)).thenReturn(false);

        ResponseEntity<?> response = authController.refreshAccessToken(INVALID_TOKEN, this.response);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(jwtUtil, never()).generateAccessToken(any());
    }

    @Test
    void refreshAccessToken_WithNullToken_ShouldReturnUnauthorized() {
        ResponseEntity<?> response = authController.refreshAccessToken(null, this.response);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(jwtUtil, never()).generateAccessToken(any());
    }
}
