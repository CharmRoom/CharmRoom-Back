package com.charmroom.charmroom.dto.business;

import com.charmroom.charmroom.entity.Subscribe;

public class SubscribeMapper {
    public static SubscribeDto toDto(Subscribe entity) {
        return SubscribeDto.builder()
                .subscriber(UserMapper.toDto(entity.getSubscriber()))
                .target(UserMapper.toDto(entity.getTarget()))
                .build();
    }
}
