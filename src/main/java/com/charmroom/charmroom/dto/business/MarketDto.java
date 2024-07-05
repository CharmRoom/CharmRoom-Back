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
    private UserDto user;
    private BoardDto board;
    private String title;
    private String body;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer view;
    @Builder.Default
    private List<CommentDto> commentList = new ArrayList<>();
    @Builder.Default
    private List<AttachmentDto> attachmentList = new ArrayList<>();
    private Integer price;
    private MarketArticleState state;
    private String tag;
}
