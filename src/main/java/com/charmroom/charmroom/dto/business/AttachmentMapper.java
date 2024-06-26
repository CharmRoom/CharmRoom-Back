package com.charmroom.charmroom.dto.business;

import com.charmroom.charmroom.entity.Attachment;

public class AttachmentMapper {
	public static AttachmentDto toDto(Attachment entity) {
		AttachmentDto dto = AttachmentDto.builder()
				.id(entity.getId())
				.type(entity.getType())
				.path(entity.getPath())
				.originalName(entity.getOriginalName())
				.build();
		if (entity.getArticle() != null) {
			ArticleDto articleDto = ArticleMapper.toDto(entity.getArticle());
			articleDto.setAttachmentList(null);
			dto.setArticle(articleDto);
		}
		return dto;
	}
}
