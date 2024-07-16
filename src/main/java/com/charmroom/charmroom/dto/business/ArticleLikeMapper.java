package com.charmroom.charmroom.dto.business;

import com.charmroom.charmroom.entity.ArticleLike;
import com.charmroom.charmroom.dto.presentation.ArticleLikeDto.ArticleLikeResponseDto;

public class ArticleLikeMapper {

    public static ArticleLikeDto toDto(ArticleLike entity) {
        return ArticleLikeDto.builder()
                .article(ArticleMapper.toDto(entity.getArticle()))
                .user(UserMapper.toDto(entity.getUser()))
                .type(entity.getType())
                .build();
    }

    public static ArticleLikeResponseDto toResponse(ArticleLikeDto dto) {
        if (dto == null) {
            return null;
        }
        return ArticleLikeResponseDto.builder()
                .type(dto.getType())
                .build();
    }
}
