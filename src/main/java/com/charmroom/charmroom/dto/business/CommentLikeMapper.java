package com.charmroom.charmroom.dto.business;

import java.util.List;

import com.charmroom.charmroom.dto.presentation.CommentLikeDto.CommentLikeResponseDto;
import com.charmroom.charmroom.entity.CommentLike;

import io.jsonwebtoken.lang.Arrays;

public class CommentLikeMapper {

	public static CommentLikeDto toDto(CommentLike entity, String... ignore) {
		List<String> ignores = Arrays.asList(ignore);
		
		CommentLikeDto dto = CommentLikeDto.builder()
				.type(entity.getType())
				.build();
		
		if (entity.getUser() != null && !ignores.contains("user"))
			dto.setUser(UserMapper.toDto(entity.getUser()));
		if (entity.getComment() != null && !ignores.contains("comment"))
			dto.setComment(CommentMapper.toDto(entity.getComment(), "commentLike"));
		
		return dto;
	}
	
	public static CommentLikeResponseDto toResponse(CommentLikeDto dto) {
		if (dto == null)
			return null;
		CommentLikeResponseDto response = CommentLikeResponseDto.builder()
				.type(dto.getType())
				.build();
		if (dto.getUser() != null)
			response.setUsername(dto.getUser().getUsername());
		return response;
	}
}
