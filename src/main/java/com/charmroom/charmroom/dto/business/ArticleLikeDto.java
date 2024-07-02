package com.charmroom.charmroom.dto.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ArticleLikeDto {
    private ArticleDto article;
    private UserDto user;
    private Boolean type;
}
