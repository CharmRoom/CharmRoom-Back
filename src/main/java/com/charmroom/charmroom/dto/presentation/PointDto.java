package com.charmroom.charmroom.dto.presentation;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class PointDto {
	@Data
	@AllArgsConstructor
	@Builder
	public static class PointResponseDto{
		private Integer id;
		private String username;
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
		private LocalDateTime updatedAt;
		private String type;
		private Integer diff;
	}
}
