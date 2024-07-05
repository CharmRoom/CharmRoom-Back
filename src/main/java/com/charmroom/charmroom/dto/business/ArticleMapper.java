package com.charmroom.charmroom.dto.business;

import java.util.ArrayList;
import java.util.List;

import com.charmroom.charmroom.dto.presentation.ArticleDto.ArticleResponseDto;
import com.charmroom.charmroom.entity.Article;

import io.jsonwebtoken.lang.Arrays;

public class ArticleMapper {
	public static ArticleDto toDto(Article entity, String... ignore) {
		ArticleDto dto = ArticleDto.builder()
				.id(entity.getId())
				.title(entity.getTitle())
				.body(entity.getBody())
				.createdAt(entity.getCreatedAt())
				.updatedAt(entity.getUpdatedAt())
				.view(entity.getView())
				.build();
		List<String> ignores = Arrays.asList(ignore);
		if (entity.getUser() != null && !ignores.contains("user")) {
			dto.setUser(UserMapper.toDto(entity.getUser()));
		}
		if (entity.getBoard() != null && !ignores.contains("board")) {
			dto.setBoard(BoardMapper.toDto(entity.getBoard()));
		}
		if (entity.getCommentList().size() > 0 && !ignores.contains("commentList")) {
			var commentList = entity.getCommentList();
			List<CommentDto> commentDtoList = new ArrayList<>();
			for(var comment : commentList) {
				CommentDto commentDto = CommentMapper.toDto(comment, "article");
				commentDtoList.add(commentDto);
			}
			dto.setCommentList(commentDtoList);
		}
		if (entity.getAttachmentList().size() > 0 && !ignores.contains("attachmentList")) {
			var attachmentList = entity.getAttachmentList();
			List<AttachmentDto> attachmentDtoList = new ArrayList<>();
			for(var attachment : attachmentList) {
				AttachmentDto attachmentDto = AttachmentMapper.toDto(attachment, "article");
				attachmentDtoList.add(attachmentDto);
			}
			dto.setAttachmentList(attachmentDtoList);
		}
		return dto;
	}
	
	public static ArticleResponseDto toResponse(ArticleDto dto) {
		return ArticleResponseDto.builder()
				.id(dto.getId())
				.build();
	}
}
