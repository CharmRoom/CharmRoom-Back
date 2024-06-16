package com.charmroom.charmroom.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.charmroom.charmroom.dto.CustomUserDetails;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.entity.enums.UserLevel;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

	private final JWTUtil jwtUtil;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String authorization = request.getHeader("Authorization");
		// 헤더 검증
		if (authorization == null || !authorization.startsWith("Bearer ")) {
			System.out.println("token null");
			filterChain.doFilter(request, response);
			return; // 헤더 없으면 다음 필터로
		}
		
		System.out.println("authorization now");
		String token = authorization.split(" ")[1];
		if (jwtUtil.isExpired(token)) {
			System.out.println("token expired");
			filterChain.doFilter(request, response);
			return; // 소멸 시간 검증 후 시간 초과면 다음 필터로
		}
		
		String username = jwtUtil.getUsername(token);
		String role = jwtUtil.getRole(token);
		
		// User 엔티티 생성
		User user = User.builder()
				.username(username)
				.password("fake_password")
				.level(UserLevel.valueOf(role))
				.build();
		// UserDetails에 담기
		CustomUserDetails customUserDetails = new CustomUserDetails(user);
		
		// Spring Security 인증 토큰 생성
		Authentication authToken = new UsernamePasswordAuthenticationToken(
				customUserDetails, null, customUserDetails.getAuthorities());
		// 세션에 사용자 등록
		SecurityContextHolder.getContext().setAuthentication(authToken);
		// 다음 필터로
		filterChain.doFilter(request, response);
		
	}
}
