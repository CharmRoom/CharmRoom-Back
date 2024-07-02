package com.charmroom.charmroom.dto.business;

import java.util.ArrayList;
import java.util.List;

import com.charmroom.charmroom.dto.presentation.CommentDto.CommentResponseDto;
import com.charmroom.charmroom.entity.Comment;

public class CommentMapper {
	public static CommentDto toDto(Comment entity) {
		CommentDto dto = CommentDto.builder()
				.id(entity.getId())
				.body(entity.getBody())
				.createdAt(entity.getCreatedAt())
				.updatedAt(entity.getUpdatedAt())
				.disabled(entity.isDisabled())
				.build();
		if (entity.getUser() != null) {
			UserDto userDto = UserMapper.toDto(entity.getUser());
			dto.setUser(userDto);
		}
		if (entity.getArticle() != null) {
			ArticleDto articleDto = ArticleMapper.toDto(entity.getArticle());
			articleDto.setCommentList(null);
			dto.setArticle(articleDto);
		}
		if (entity.getParent() != null) {
			CommentDto parent = CommentMapper.toDto(entity.getParent());
			parent.setChildList(null);
			dto.setParent(parent);
		}
		
		for(var child : entity.getChildList()) {
			CommentDto childDto = CommentMapper.toDto(child);
			childDto.setParent(null);
			dto.getChildList().add(childDto);
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
		if (dto.getParent() != null)
			response.setParentId(dto.getParent().getId());
		if (dto.getChildList().size() > 0) {
			var childList = dto.getChildList().stream().map(child -> toResponse(child)).toList();
			response.setChildList(childList);
		}
		
		return response;
	}
}
