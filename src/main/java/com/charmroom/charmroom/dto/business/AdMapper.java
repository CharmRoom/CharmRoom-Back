package com.charmroom.charmroom.dto.business;

import com.charmroom.charmroom.entity.Ad;
import com.charmroom.charmroom.dto.presentation.AdDto.AdResponseDto;

public class AdMapper {
    public static AdDto toDto(Ad entity) {
        AdDto dto = AdDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .start(entity.getStart())
                .end(entity.getEnd())
                .link(entity.getLink())
                .build();

        if (entity.getImage() != null) {
            dto.setImage(ImageMapper.toDto(entity.getImage()));
        }

        return dto;
    }

    public static AdResponseDto toResponse(AdDto dto) {
        return AdResponseDto.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .start(dto.getStart())
                .start(dto.getStart())
                .end(dto.getEnd())
                .image(ImageMapper.toResponse(dto.getImage()))
                .build();
    }
}
