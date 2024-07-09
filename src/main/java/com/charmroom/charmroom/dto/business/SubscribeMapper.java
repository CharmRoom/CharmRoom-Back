package com.charmroom.charmroom.dto.business;

import com.charmroom.charmroom.entity.Subscribe;
import com.charmroom.charmroom.dto.presentation.SubscribeDto.SubscribeResponseDto;

public class SubscribeMapper {
    public static SubscribeDto toDto(Subscribe entity) {
        return SubscribeDto.builder()
                .subscriber(UserMapper.toDto(entity.getSubscriber()))
                .target(UserMapper.toDto(entity.getTarget()))
                .build();
    }

    public static SubscribeResponseDto toResponse(SubscribeDto dto) {
        return SubscribeResponseDto.builder()
                .id(dto.getId())
                .target(UserMapper.toResponse(dto.getTarget()))
                .subscriber(UserMapper.toResponse(dto.getSubscriber()))
                .build();
    }

}
