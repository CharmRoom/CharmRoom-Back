package com.charmroom.charmroom.dto.business;

import java.util.ArrayList;
import java.util.List;

import com.charmroom.charmroom.entity.Comment;

public class CommentMapper {
	public static CommentDto toDto(Comment entity) {
		CommentDto dto = CommentDto.builder()
				.id(entity.getId())
				.body(entity.getBody())
				.createdAt(entity.getCreatedAt())
				.updatedAt(entity.getUpdatedAt())
				.disabled(entity.getDisabled())
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
		if (entity.getCommentLike().size() > 0) {
			var commentLikes = entity.getCommentLike();
			List<CommentLikeDto> commentLikeDtoList = new ArrayList<>();
			for(var commentLike : commentLikes) {
				CommentLikeDto commentLikeDto = CommentLikeMapper.toDto(commentLike);
				commentLikeDto.setComment(null);
				commentLikeDtoList.add(commentLikeDto);
			}
			dto.setCommentLike(commentLikeDtoList);
		}
		return dto;
	}
}
