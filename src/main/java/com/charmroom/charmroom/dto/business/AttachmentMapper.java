package com.charmroom.charmroom.dto.business;

import java.util.List;

import com.charmroom.charmroom.entity.Attachment;

import io.jsonwebtoken.lang.Arrays;

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
}
