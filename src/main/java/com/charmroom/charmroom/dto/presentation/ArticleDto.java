package com.charmroom.charmroom.dto.presentation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class ArticleDto {
	@Data
	@AllArgsConstructor
	@Builder
	public static class ArticleResponseDto{
		private Integer id;
		
	}
}
