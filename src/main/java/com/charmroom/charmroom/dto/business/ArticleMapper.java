package com.charmroom.charmroom.dto.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.charmroom.charmroom.dto.presentation.ArticleDto.ArticleResponseDto;
import com.charmroom.charmroom.entity.Article;

public class ArticleMapper {

	public static ArticleDto toDto(Article entity, String... ignore) {
		List<String> ignores = Arrays.asList(ignore);

		ArticleDto dto = ArticleDto.builder()
				.id(entity.getId())
				.title(entity.getTitle())
				.body(entity.getBody())
				.createdAt(entity.getCreatedAt())
				.updatedAt(entity.getUpdatedAt())
				.view(entity.getView())
				.build();
		if (entity.getUser() != null && !ignores.contains("user")) {
			dto.setUser(UserMapper.toDto(entity.getUser()));
		}
		if (entity.getBoard() != null && ignores.contains("board")) {
			dto.setBoard(BoardMapper.toDto(entity.getBoard()));
		}
		if (!entity.getCommentList().isEmpty() && ignores.contains("commentList")) {
			var commentList = entity.getCommentList();
			List<CommentDto> commentDtoList = new ArrayList<>();
			for(var comment : commentList) {
				CommentDto commentDto = CommentMapper.toDto(comment, "article");
				commentDtoList.add(commentDto);
			}
			dto.setCommentList(commentDtoList);
		}
		if (!entity.getAttachmentList().isEmpty() && !ignores.contains("attachmentList")) {
			var attachmentList = entity.getAttachmentList();
			List<AttachmentDto> attachmentDtoList = new ArrayList<>();
			for(var attachment : attachmentList) {
				AttachmentDto attachmentDto = AttachmentMapper.toDto(attachment);
				attachmentDto.setArticle(null);
				attachmentDtoList.add(attachmentDto);
			}
			dto.setAttachmentList(attachmentDtoList);
		}
		return dto;
	}
	
	public static ArticleResponseDto toResponse(ArticleDto dto) {
		ArticleResponseDto responseDto = ArticleResponseDto.builder()
				.id(dto.getId())
				.title(dto.getTitle())
				.body(dto.getBody())
				.view(dto.getView())
				.createdAt(dto.getCreatedAt())
				.updatedAt(dto.getUpdatedAt())
				.build();

		responseDto.setFiles(dto.getAttachmentList()
				.stream().map(attachment -> AttachmentMapper.toResponse(attachment)).toList());

		if (dto.getUser() != null) {
			responseDto.setUser(UserMapper.toResponse(dto.getUser()));
		}

		if(dto.getBoard() != null) {
			responseDto.setBoard(BoardMapper.toResponse(dto.getBoard()));
		}

		if(!dto.getCommentList().isEmpty()) {
			var commentList = dto.getCommentList();
			var commentResponseList = commentList.stream().map(comment -> CommentMapper.toResponse(comment)).toList();
			responseDto.setComments(commentResponseList);
		}

		return responseDto;
	}
}
