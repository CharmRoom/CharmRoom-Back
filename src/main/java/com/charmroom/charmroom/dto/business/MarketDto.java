package com.charmroom.charmroom.dto.business;

import com.charmroom.charmroom.entity.enums.MarketArticleState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Builder
@Data
public class MarketDto {
    private Integer id;
    private ArticleDto article;
    private Integer price;
    private MarketArticleState state;
    private String tag;
}
