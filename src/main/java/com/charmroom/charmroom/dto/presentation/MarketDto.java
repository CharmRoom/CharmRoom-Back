package com.charmroom.charmroom.dto.presentation;

import com.charmroom.charmroom.entity.enums.MarketArticleState;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class MarketDto {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MarketCreateRequestDto {
        @NotNull
        private ArticleDto.ArticleCreateRequestDto article;
        @NotNull
        private Integer price;
        @NotNull
        private MarketArticleState state;
        @NotBlank
        private String tag;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MarketUpdateRequestDto {
        @NotNull
        private ArticleDto.ArticleUpdateRequestDto article;
        @NotNull
        private Integer price;
        @NotNull
        private MarketArticleState state;
        @NotBlank
        private String tag;
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class MarketResponseDto {
        private Integer id;
        private ArticleDto.ArticleResponseDto article;
        private Integer price;
        private MarketArticleState state;
        private String tag;
    }
}
