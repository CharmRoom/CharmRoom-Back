package com.charmroom.charmroom.dto.presentation;

import com.charmroom.charmroom.dto.validation.ValidBoardType;

import jakarta.validation.constraints.NotEmpty;
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
		@NotEmpty
		private String name;
		@ValidBoardType
		private String type;
	}
	
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class BoardUpdateRequestDto{
		@NotEmpty
		private String name;
		@ValidBoardType
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
