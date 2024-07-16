package com.charmroom.charmroom.dto.presentation;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public class ArticleDto {
	@Data
	@AllArgsConstructor
	@Builder
	public static class ArticleResponseDto{
		private Integer id;
		private BoardDto.BoardResponseDto board;
		private UserDto.UserResponseDto user;
		private String title;
		private String body;
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
		private LocalDateTime createdAt;
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
		private LocalDateTime updatedAt;
		private Integer view;
		private List<CommentDto.CommentResponseDto> comments;
		private List<AttachmentDto.AttachmentResponseDto> files;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class ArticleCreateRequestDto{
		private String title;
		private String body;
		private List<MultipartFile> file;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class ArticleUpdateRequestDto{
		private String title;
		private String body;
	}
}
