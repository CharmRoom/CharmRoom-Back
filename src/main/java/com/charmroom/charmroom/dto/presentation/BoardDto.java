package com.charmroom.charmroom.dto.presentation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class BoardDto {
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class BoardCreateRequestDto{
		private String name;
		private String type;
	}
	
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class BoardUpdateRequestDto{
		private String name;
		private String type;
	}

	@Data
	@AllArgsConstructor
	@Builder
	public static class BoardResponseDto{
		private Integer id;
		private String name;
		private String type;
		private boolean exposed;
	}
}
