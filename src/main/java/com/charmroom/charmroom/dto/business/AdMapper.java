package com.charmroom.charmroom.dto.business;

import com.charmroom.charmroom.entity.Ad;

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
}
