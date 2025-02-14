package com.charmroom.charmroom.config;

import com.charmroom.charmroom.oauth2.CustomSuccessHandler;
import com.charmroom.charmroom.service.oauth2.CustomOAuth2UserService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.charmroom.charmroom.repository.RefreshTokenRepository;
import com.charmroom.charmroom.security.JWTFilter;
import com.charmroom.charmroom.security.JWTUtil;
import com.charmroom.charmroom.security.LoginFilter;
import com.charmroom.charmroom.service.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;

@EnableMethodSecurity
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final AuthenticationConfiguration authenticationConfiguration;
	private final JWTUtil jwtUtil;
	private final CustomUserDetailsService customUserDetailsService;
	private final RefreshTokenRepository refreshTokenRepository;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final CustomSuccessHandler customSuccessHandler;
	private final CorsConfig corsConfig;

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		// CSRF disable
		http.csrf((csrf) -> csrf.disable());
		
		http.cors((corsCustomizer -> corsCustomizer.configurationSource(corsConfig)));
		
		// formLogin disable
		http.formLogin((formLogin) -> formLogin.disable());
		
		// HTTP basic 인증 disable
		http.httpBasic((httpBasic) -> httpBasic.disable());

		// OAuth2
		http.oauth2Login((oauth2) -> oauth2
				.userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
						.userService(customOAuth2UserService))
				.successHandler(customSuccessHandler)
		);
		
		// JWTFilter 등록
		http.addFilterBefore(
				new JWTFilter(jwtUtil, customUserDetailsService),
				LoginFilter.class);
		
		// 로그인 필터 추가
		LoginFilter loginFilter = new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, refreshTokenRepository);
		loginFilter.setFilterProcessesUrl("/api/auth/login"); // POST
		http.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);
		
		// 세션 설정
		http.sessionManagement((session) -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		
		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
}
