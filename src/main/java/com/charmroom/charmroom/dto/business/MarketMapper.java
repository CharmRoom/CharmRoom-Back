package com.charmroom.charmroom.dto.business;

import com.charmroom.charmroom.entity.Attachment;
import com.charmroom.charmroom.entity.Comment;
import com.charmroom.charmroom.entity.Market;
import  com.charmroom.charmroom.dto.presentation.MarketDto.MarketResponseDto;

import java.util.ArrayList;
import java.util.List;

public class MarketMapper {
    // MarketEntity -> MarketDto 변환
    public static MarketDto toDto(Market entity){
        return MarketDto.builder()
                .id(entity.getId())
                .article(ArticleMapper.toDto(entity.getArticle()))
                .tag(entity.getTag())
                .state(entity.getState())
                .price(entity.getPrice())
                .build();
    }

    public static MarketResponseDto toResponse(MarketDto dto) {
       return MarketResponseDto.builder()
                .id(dto.getId())
                .article(ArticleMapper.toResponse(dto.getArticle()))
                .price(dto.getPrice())
                .state(dto.getState())
                .tag(dto.getTag())
                .build();
    }
}
