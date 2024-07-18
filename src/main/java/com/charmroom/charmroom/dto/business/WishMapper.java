package com.charmroom.charmroom.dto.business;

import com.charmroom.charmroom.entity.Wish;
import com.charmroom.charmroom.dto.presentation.WishDto.WishResponseDto;

public class WishMapper {

    public static WishDto doDto(Wish entity) {
        return WishDto.builder()
                .market(MarketMapper.toDto(entity.getMarket()))
                .user(UserMapper.toDto(entity.getUser()))
                .build();
    }

    public static WishResponseDto toResponse(WishDto dto) {
        if (dto == null) {
            return null;
        }

        return WishResponseDto.builder()
                .id(dto.getId())
                .user(UserMapper.toResponse(dto.getUser()))
                .market(MarketMapper.toResponse(dto.getMarket()))
                .build();
    }
}
