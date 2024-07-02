package com.charmroom.charmroom.dto.business;

import com.charmroom.charmroom.entity.Attachment;
import com.charmroom.charmroom.entity.Comment;
import com.charmroom.charmroom.entity.enums.MarketArticleState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Builder
@Data
public class MarketDto {
    private final Integer id;
    private final String username;
    private final Integer boardId;
    private final String title;
    private final String body;
    private final Integer view;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final List<Comment> commentList;
    private final List<Attachment> attachmentList;
    private final Integer price;
    private final MarketArticleState state;
    private final String tag;
}
