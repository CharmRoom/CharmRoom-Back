package com.charmroom.charmroom.dto.presentation;

import com.charmroom.charmroom.entity.enums.MarketArticleState;

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
        private ArticleDto.ArticleCreateRequestDto article;
        private Integer price;
        private MarketArticleState state;
        private String tag;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MarketUpdateRequestDto {
       private ArticleDto.ArticleUpdateRequestDto article;
        private Integer price;
        private MarketArticleState state;
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
