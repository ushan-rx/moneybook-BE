package com.moneybook.config.security;

import com.moneybook.dto.user.NormalUserCreateDto;
import com.moneybook.dto.user.NormalUserDto;
import com.moneybook.service.NormalUserService;
import com.moneybook.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final NormalUserService userService;
    private final JwtUtil jwtUtil;

    public OAuth2SuccessHandler(NormalUserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        // Get user details from OAuth2User
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String subId = oauthUser.getAttribute("sub");

        // Generate tokens
        String accessToken = jwtUtil.generateAccessToken(subId);
        String refreshToken = jwtUtil.generateRefreshToken(subId);

        // Set tokens in HTTP-only cookies
        JwtUtil.setJwtCookies(response, accessToken, refreshToken);

        boolean isNewUser = userService.isNewUser(subId);
        System.out.println("isNewUser: " + isNewUser);
        if(isNewUser) {
            NormalUserDto user = saveNewUser(oauthUser);
            if(user == null) {
                response.sendRedirect("http://localhost:3000/error");
                return;
            }
            response.sendRedirect("http://localhost:3000/onboarding");
            return;
        }

        // Redirect user to home if not new
        response.sendRedirect("http://localhost:3000/home");
    }

    private NormalUserDto saveNewUser(OAuth2User oauthUser) {
        // save new user
        NormalUserCreateDto userDto = new NormalUserCreateDto();
        userDto.setUserId(oauthUser.getAttribute("sub"));
        userDto.setEmail(oauthUser.getAttribute("email"));
        userDto.setProfilePicture(oauthUser.getAttribute("picture"));
        return userService.saveNormalUser(userDto);
    }
}
