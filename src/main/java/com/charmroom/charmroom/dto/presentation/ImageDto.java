package com.charmroom.charmroom.dto.presentation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


public class ImageDto {
	@Data
	@AllArgsConstructor
	@Builder
	public static class ImageResponseDto{
		private Integer id;
		private String originalName;
		private String path;
	}
}
