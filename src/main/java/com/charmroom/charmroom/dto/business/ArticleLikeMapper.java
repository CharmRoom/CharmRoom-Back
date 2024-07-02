package com.charmroom.charmroom.dto.business;

import com.charmroom.charmroom.entity.ArticleLike;

public class ArticleLikeMapper {

    public static ArticleLikeDto toDto(ArticleLike entity) {
        return ArticleLikeDto.builder()
                .article(ArticleMapper.toDto(entity.getArticle()))
                .user(UserMapper.toDto(entity.getUser()))
                .type(entity.getType())
                .build();
    }
}
