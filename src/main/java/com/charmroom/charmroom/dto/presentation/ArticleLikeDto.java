package com.charmroom.charmroom.dto.presentation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class ArticleLikeDto {
    @Data
    @AllArgsConstructor
    @Builder
    public static class ArticleLikeResponseDto {
        private Boolean type;
    }
}
