package com.charmroom.charmroom.dto.presentation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class WishDto {
    @Data
    @AllArgsConstructor
    @Builder
    public static class WishResponseDto {
        private Integer id;
        private UserDto.UserResponseDto user;
        private MarketDto.MarketResponseDto market;
    }
}
