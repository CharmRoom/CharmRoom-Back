package com.charmroom.charmroom.oauth2;

import com.charmroom.charmroom.dto.oauth2.CustomOAuth2User;
import com.charmroom.charmroom.entity.RefreshToken;
import com.charmroom.charmroom.repository.RefreshTokenRepository;
import com.charmroom.charmroom.security.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        String username = customUserDetails.getUsername();

        String role = authentication.getAuthorities().iterator().next().getAuthority();

        String access = jwtUtil.createJwt("access", username, role, 600000L);
        String refresh = jwtUtil.createJwt("refresh", username, role, 86400000L);
        refreshTokenRepository.save(RefreshToken.builder().token(refresh).username(username).build());

        response.addCookie(createCookie("access", access, 600000));
        response.addCookie(createCookie("refresh", refresh, 86400000));

        response.sendRedirect("http://localhost:8080/");
    }

    private Cookie createCookie(String key, String value, int exp) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(exp);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
