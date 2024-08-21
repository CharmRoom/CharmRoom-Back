package com.charmroom.charmroom.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

public class CorsMvcConfig implements WebMvcConfigurer {
	
	@Value("${charmroom.cors.allowd-origins}")
	private String allowedOrigins;
	
	@Override
	public void addCorsMappings(CorsRegistry corsRegistry) {
		corsRegistry.addMapping("/**").allowedOrigins(allowedOrigins);
	}
}
