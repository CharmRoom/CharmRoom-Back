package com.charmroom.charmroom.dto.presentation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.charmroom.charmroom.dto.presentation.UserDto.UserResponseDto;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CommentDto {
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class CommentCreateRequestDto{
		private String body;
		private Integer parentId;
	}
	
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class CommentUpdateRequestDto{
		private String body;
	}
	
	@Data
	@AllArgsConstructor
	@Builder
	public static class CommentResponseDto{
		private Integer id;
		private UserResponseDto user;
		private Integer articleId;
		private CommentResponseDto parent;
		@Builder.Default
		private List<CommentResponseDto> childList = new ArrayList<>();
		private String body;
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
		private LocalDateTime createdAt;
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
		private LocalDateTime updatedAt;
		private boolean disabled;
		private Integer like;
		private Integer dislike;
		private Boolean userLikeType;
	}
}
