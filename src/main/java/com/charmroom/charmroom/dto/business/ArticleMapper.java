package com.charmroom.charmroom.dto.business;

import java.util.ArrayList;
import java.util.List;

import com.charmroom.charmroom.entity.Article;

public class ArticleMapper {

	public static ArticleDto toDto(Article entity) {
		ArticleDto dto = ArticleDto.builder()
				.id(entity.getId())
				.title(entity.getTitle())
				.body(entity.getBody())
				.createdAt(entity.getCreatedAt())
				.updatedAt(entity.getUpdatedAt())
				.view(entity.getView())
				.build();
		if (entity.getUser() != null) {
			dto.setUser(UserMapper.toDto(entity.getUser()));
		}
		if (entity.getBoard() != null) {
			dto.setBoard(BoardMapper.toDto(entity.getBoard()));
		}
		if (entity.getCommentList().size() > 0) {
			var commentList = entity.getCommentList();
			List<CommentDto> commentDtoList = new ArrayList<>();
			for(var comment : commentList) {
				CommentDto commentDto = CommentMapper.toDto(comment);
				commentDto.setArticle(null);
				commentDtoList.add(commentDto);
			}
			dto.setCommentList(commentDtoList);
		}
		if (entity.getAttachmentList().size() > 0) {
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

}
