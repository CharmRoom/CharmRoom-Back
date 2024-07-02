package com.charmroom.charmroom.dto.business;

import com.charmroom.charmroom.entity.Market;

public class MarketMapper {
    // MarketEntity -> MarketDto 변환
    public static MarketDto toDto(Market entity){
        return MarketDto.builder()
                .id(entity.getId())
                .username(entity.getArticle().getUser().getUsername()) // 예시: User 엔티티에서 id 가져오기
                .boardId(entity.getArticle().getBoard().getId())
                .title(entity.getArticle().getTitle())
                .body(entity.getArticle().getBody())
                .view(entity.getArticle().getView())
                .createdAt(entity.getArticle().getCreatedAt())
                .updatedAt(entity.getArticle().getUpdatedAt())
                .commentList(entity.getArticle().getCommentList())
                .attachmentList(entity.getArticle().getAttachmentList())
                .tag(entity.getTag())
                .state(entity.getState())
                .price(entity.getPrice())
                .build();
    }
}
