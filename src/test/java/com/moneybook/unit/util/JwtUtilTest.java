package com.moneybook.unit.util;

import com.moneybook.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletResponse mockResponse;

    private static final String TEST_SECRET_KEY = "testSecretKeyWithAtLeast32Characters12345";
    private static final String TEST_REFRESH_SECRET_KEY = "testRefreshSecretKeyWithAtLeast32Chars12";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secretKey", TEST_SECRET_KEY);
        ReflectionTestUtils.setField(jwtUtil, "refreshSecretKey", TEST_REFRESH_SECRET_KEY);
    }

    @Test
    void generateAccessToken_ShouldCreateValidToken() {
        String userId = "123";
        String token = jwtUtil.generateAccessToken(userId);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(jwtUtil.validateAccessToken(token));
    }

    @Test
    void generateRefreshToken_ShouldCreateValidToken() {
        String userId = "123";
        String token = jwtUtil.generateRefreshToken(userId);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void setJwtCookies_ShouldSetBothCookies() {
        String accessToken = "test-access-token";
        String refreshToken = "test-refresh-token";

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);

        JwtUtil.setJwtCookies(mockResponse, accessToken, refreshToken);

        verify(mockResponse, times(2)).addCookie(cookieCaptor.capture());

        var cookies = cookieCaptor.getAllValues();
        assertEquals("auth-token", cookies.get(0).getName());
        assertEquals(accessToken, cookies.get(0).getValue());
        assertEquals("refresh-token", cookies.get(1).getName());
        assertEquals(refreshToken, cookies.get(1).getValue());

        assertTrue(cookies.get(0).isHttpOnly());
        assertTrue(cookies.get(1).isHttpOnly());
    }

    @Test
    void setAccessTokenCookie_ShouldSetOnlyAccessToken() {
        String accessToken = "test-access-token";

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);

        JwtUtil.setAccessTokenCookie(mockResponse, accessToken);

        verify(mockResponse).addCookie(cookieCaptor.capture());

        Cookie cookie = cookieCaptor.getValue();
        assertEquals("auth-token", cookie.getName());
        assertEquals(accessToken, cookie.getValue());
        assertTrue(cookie.isHttpOnly());
    }

    @Test
    void clearCookies_ShouldClearBothCookies() {
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);

        JwtUtil.clearCookies(mockResponse);

        verify(mockResponse, times(2)).addCookie(cookieCaptor.capture());

        var cookies = cookieCaptor.getAllValues();
        assertEquals(0, cookies.get(0).getMaxAge());
        assertEquals(0, cookies.get(1).getMaxAge());
        assertNull(cookies.get(0).getValue());
        assertNull(cookies.get(1).getValue());
    }

    @Test
    void validateAccessToken_ShouldReturnFalseForInvalidToken() {
        assertFalse(jwtUtil.validateAccessToken("invalid-token"));
    }
}
