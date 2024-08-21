package com.charmroom.charmroom.config;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class CorsConfig implements CorsConfigurationSource {

	@Value("${charmroom.cors.allowed-origins}")
	private String allowedOrigins;
	
	@Override
	public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Collections.singletonList(allowedOrigins));
		configuration.setAllowedMethods(Collections.singletonList("*"));
		configuration.setAllowCredentials(true);
		configuration.setAllowedHeaders(Collections.singletonList("*"));
		configuration.setMaxAge(3600L);
		configuration.setExposedHeaders(Collections.singletonList("Authorization"));
		return configuration;
	}
}
