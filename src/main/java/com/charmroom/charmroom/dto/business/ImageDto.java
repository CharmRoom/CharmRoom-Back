package com.charmroom.charmroom.dto.business;

import org.springframework.core.io.Resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ImageDto {
	private Integer id;
	private String path;
	private String originalName;
	private Resource resource;
}
