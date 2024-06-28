package com.charmroom.charmroom.dto.business;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CommentDto {
	private Integer id;
	private UserDto user;
	private ArticleDto article;
	private CommentDto parent;
	private List<CommentDto> childList;
	private String body;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private Boolean disabled;
	private List<CommentLikeDto> commentLike;
}
