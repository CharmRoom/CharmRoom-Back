package com.charmroom.charmroom.dto.business;

import java.util.Arrays;
import java.util.List;
import com.charmroom.charmroom.entity.Attachment;
import com.charmroom.charmroom.dto.presentation.AttachmentDto.AttachmentResponseDto;
import com.charmroom.charmroom.dto.presentation.ArticleDto.ArticleResponseDto;

public class AttachmentMapper {
	public static AttachmentDto toDto(Attachment entity, String... ignore) {
		AttachmentDto dto = AttachmentDto.builder()
				.id(entity.getId())
				.type(entity.getType())
				.path(entity.getPath())
				.originalName(entity.getOriginalName())
				.build();
		List<String> ignores = Arrays.asList(ignore);
		if (entity.getArticle() != null && !ignores.contains("article")) {
			ArticleDto articleDto = ArticleMapper.toDto(entity.getArticle(), "attachmentList");
			dto.setArticle(articleDto);
		}
		return dto;
	}

	public static AttachmentResponseDto toResponse(AttachmentDto dto) {
		AttachmentResponseDto responseDto = AttachmentResponseDto.builder()
				.id(dto.getId())
				.type(dto.getType().toString())
				.path(dto.getPath())
				.originalName(dto.getOriginalName())
				.build();

		if (dto.getArticle() != null) {
			ArticleResponseDto articleResponseDto = ArticleMapper.toResponse(dto.getArticle());
			responseDto.setArticle(articleResponseDto);
		}

		return responseDto;
	}
}
