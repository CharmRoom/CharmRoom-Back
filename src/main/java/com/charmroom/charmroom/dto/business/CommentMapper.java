package com.charmroom.charmroom.dto.business;

import java.util.List;

import com.charmroom.charmroom.dto.presentation.CommentDto.CommentResponseDto;
import com.charmroom.charmroom.entity.Comment;

import io.jsonwebtoken.lang.Arrays;

public class CommentMapper {
	
	public static CommentDto toDto(Comment entity, String... ignore) {
		CommentDto dto = CommentDto.builder()
				.id(entity.getId())
				.body(entity.getBody())
				.createdAt(entity.getCreatedAt())
				.updatedAt(entity.getUpdatedAt())
				.disabled(entity.isDisabled())
				.build();
		List<String> ignores = Arrays.asList(ignore);
		if (entity.getUser() != null && !ignores.contains("user")) {
			UserDto userDto = UserMapper.toDto(entity.getUser());
			dto.setUser(userDto);
		}
		if (entity.getArticle() != null && !ignores.contains("article")) {
			ArticleDto articleDto = ArticleMapper.toDto(entity.getArticle(), "commentList");
			dto.setArticle(articleDto);
		}
		if (entity.getParent() != null && !ignores.contains("parent")) {
			CommentDto parent = CommentMapper.toDto(entity.getParent(), "childList");
			dto.setParent(parent);
		}
		
		if (!ignores.contains("childList")) {
			var childList = entity.getChildList().stream().map(child -> CommentMapper.toDto(child, "parent")).toList();
			dto.setChildList(childList);
		}
		
		if (!ignores.contains("commentLike")) {
			var commentLike = entity.getCommentLike().stream().map(cl -> CommentLikeMapper.toDto(cl, "comment")).toList();
			dto.setCommentLike(commentLike);
		}
		
		return dto;
	}

	
	public static CommentResponseDto toResponse(CommentDto dto) {
		if (dto == null)
			return null;
		
		var response = CommentResponseDto.builder()
				.id(dto.getId())
				.body(dto.getBody())
				.createdAt(dto.getCreatedAt())
				.updatedAt(dto.getUpdatedAt())
				.disabled(dto.isDisabled())
				.build();
		
		if (dto.getUser() != null)
			response.setUser(UserMapper.toResponse(dto.getUser()));
		if (dto.getArticle() != null)
			response.setArticleId(dto.getArticle().getId());
		if (dto.getParent() != null) {
			var parent = toResponse(dto.getParent());
			response.setParent(parent);
		}
		
		var childList = dto.getChildList().stream().map(CommentMapper::toResponse).toList();
		response.setChildList(childList);
		
		var commentLike = dto.getCommentLike().stream().map(CommentLikeMapper::toResponse).toList();
		response.setCommentLike(commentLike);
	
		return response;
	}
}
