package com.charmroom.charmroom.dto.business;

import com.charmroom.charmroom.entity.Image;

public class ImageMapper {
	public static ImageDto toDto(Image entity) {
		if (entity == null) return null;
		
		return ImageDto.builder()
				.id(entity.getId())
				.path(entity.getPath())
				.originalName(entity.getOriginalName())
				.build();
	}
}
