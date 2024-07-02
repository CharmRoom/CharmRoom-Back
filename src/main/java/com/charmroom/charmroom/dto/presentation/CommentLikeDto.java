package com.charmroom.charmroom.dto.presentation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class CommentLikeDto {
	@Data
	@AllArgsConstructor
	@Builder
	public static class CommentLikeResponseDto{
		private Boolean type;
	}
}
