package com.charmroom.charmroom.dto.business;

import com.charmroom.charmroom.entity.Wish;

public class WishMapper {

    public static WishDto doDto(Wish entity) {
        return WishDto.builder()
                .market(MarketMapper.toDto(entity.getMarket()))
                .user(UserMapper.toDto(entity.getUser()))
                .build();
    }
}
