package com.charmroom.charmroom.dto.business;

import org.springframework.core.io.Resource;

import com.charmroom.charmroom.entity.enums.AttachmentType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
@Data
@AllArgsConstructor
@Builder
public class AttachmentDto {
	private Integer id;
	private ArticleDto article;
	private AttachmentType type;
	private String path;
	private String originalName;
	private Resource resource;
}
