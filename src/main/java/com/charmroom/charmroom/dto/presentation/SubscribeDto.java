package com.charmroom.charmroom.dto.presentation;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class SubscribeDto {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SubscribeCreateRequestDto {
        @NotBlank
        private String subscriberUserName;
        @NotBlank
        private String targetUserName;
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class SubscribeResponseDto {
        private Integer id;
        private UserDto.UserResponseDto target;
        private UserDto.UserResponseDto subscriber;
    }
}
