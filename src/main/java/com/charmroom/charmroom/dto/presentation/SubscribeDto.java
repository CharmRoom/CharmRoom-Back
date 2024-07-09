package com.charmroom.charmroom.dto.presentation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class SubscribeDto {

    @Data
    @AllArgsConstructor
    @Builder
    public static class SubscribeResponseDto {
        private Integer id;
        private UserDto.UserResponseDto target;
        private UserDto.UserResponseDto subscriber;
    }
}
