package com.charmroom.charmroom.dto.presentation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class ClubRegisterDto {
    @Data
    @AllArgsConstructor
    @Builder
    public static class ClubRegisterResponseDto {
        private Integer id;
        private UserDto.UserResponseDto user;
        private ClubDto.ClubResponseDto club;
    }
}
