package com.charmroom.charmroom.dto.business;

import com.charmroom.charmroom.entity.CommentLike;

public class CommentLikeMapper {

	public static CommentLikeDto toDto(CommentLike entity) {
		return CommentLikeDto.builder()
				.user(UserMapper.toDto(entity.getUser()))
				.comment(CommentMapper.toDto(entity.getComment()))
				.type(entity.getType())
				.build();
	}

}
