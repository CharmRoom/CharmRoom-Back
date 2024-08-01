package com.charmroom.charmroom.dto.business;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
	@Builder.Default
	private List<CommentDto> childList = new ArrayList<>();
	private String body;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private boolean disabled;
	@Builder.Default
	private List<CommentLikeDto> commentLike = new ArrayList<>();
}
