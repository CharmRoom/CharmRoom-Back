package com.charmroom.charmroom.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
public class WebConfig implements WebMvcConfigurer{
	@Value("${charmroom.upload.image.path}") 
	String imageUploadPath;
	@Value("${charmroom.upload.attachment.path}") 
	String attachmentUploadPath;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		
		if (!imageUploadPath.endsWith("/")) imageUploadPath += "/";
		if (!attachmentUploadPath.endsWith("/")) attachmentUploadPath += "/"; 
		
		registry
			.addResourceHandler("/static/image/**")
			.addResourceLocations("file:"+imageUploadPath)
			// 개발중에는 캐시 사용하지 않음
			//.setCacheControl(CacheControl.maxAge(Duration.ofHours(1L)).cachePublic())
			.resourceChain(false);
		registry
			.addResourceHandler("/static/attachment/**")
			.addResourceLocations("file:"+attachmentUploadPath)
			//.setCacheControl(CacheControl.maxAge(Duration.ofHours(1L)).cachePublic())
			.resourceChain(false);
	}
	
	
}
