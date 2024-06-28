package com.charmroom.charmroom.dto.business;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ArticleDto {
	private Integer id;
	private UserDto user;
	private BoardDto board;
	private String title;
	private String body;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private Integer view;
	private List<CommentDto> commentList;
	private List<AttachmentDto> attachmentList;
}
