package com.charmroom.charmroom.dto.presentation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class AttachmentDto {
    @Data
    @AllArgsConstructor
    @Builder
    public static class AttachmentResponseDto {
        private Integer id;
        private ArticleDto.ArticleResponseDto article;
        private String type;
        private String path;
        private String originalName;
    }
}
