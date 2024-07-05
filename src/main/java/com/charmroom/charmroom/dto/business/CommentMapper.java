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
			for(var child : entity.getChildList()) {
				CommentDto childDto = CommentMapper.toDto(child, "parent");
				dto.getChildList().add(childDto);
			}
		}
		
		Integer like = 0, dislike = 0;
		for(var cl : entity.getCommentLike()) {
			if (cl.getType())
				like++;
			else
				dislike++;
		}
		dto.setLike(like);
		dto.setDislike(dislike);
		
		return dto;
	}

	
	public static CommentResponseDto toResponse(CommentDto dto) {
		var response = CommentResponseDto.builder()
				.id(dto.getId())
				.body(dto.getBody())
				.createdAt(dto.getCreatedAt())
				.updatedAt(dto.getUpdatedAt())
				.disabled(dto.isDisabled())
				.like(dto.getLike())
				.dislike(dto.getDislike())
				.userLikeType(dto.getUserLikeType())
				.build();
		
		if (dto.getUser() != null)
			response.setUser(UserMapper.toResponse(dto.getUser()));
		if (dto.getArticle() != null)
			response.setArticleId(dto.getArticle().getId());
		if (dto.getParent() != null) {
			var parent = toResponse(dto.getParent());
			parent.getChildList().clear();
			response.setParent(parent);
		}
		if (dto.getChildList().size() > 0) {
			var childList = dto.getChildList().stream()
					.map(child -> toResponse(child))
					.toList()
					;
			childList.forEach(child -> child.setParent(null));
			response.setChildList(childList);
		}
		
		return response;
	}
}
