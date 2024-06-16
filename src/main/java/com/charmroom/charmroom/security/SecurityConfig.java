package com.charmroom.charmroom.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final AuthenticationConfiguration authenticationConfiguration;
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		// CSRF disable
		http.csrf((csrf) -> csrf.disable());
		
		// formLogin disable
		http.formLogin((formLogin) -> formLogin.disable());
		
		// HTTP basic 인증 disable
		http.httpBasic((httpBasic) -> httpBasic.disable());
		
		// 경로 별 인가 작업
		http.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
				// API permit list
				.requestMatchers("/api/auth/login", "/api/auth/signup").permitAll()
				.requestMatchers("/", "/auth/login", "/auth/signup").permitAll()
				.requestMatchers("/api/admin/**").hasRole("ADMIN")
				.anyRequest().authenticated()
				);
		
		// 로그인 필터 추가
		http.addFilterAt(
				new LoginFilter(authenticationManager(authenticationConfiguration)),
				UsernamePasswordAuthenticationFilter.class);
		
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
