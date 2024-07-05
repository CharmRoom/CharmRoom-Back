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
        MarketDto dto = MarketDto.builder()
                .id(entity.getId())
                .title(entity.getArticle().getTitle())
                .body(entity.getArticle().getBody())
                .view(entity.getArticle().getView())
                .createdAt(entity.getArticle().getCreatedAt())
                .updatedAt(entity.getArticle().getUpdatedAt())
                .tag(entity.getTag())
                .state(entity.getState())
                .price(entity.getPrice())
                .build();

        if (entity.getArticle().getUser() != null) {
            dto.setUser(UserMapper.toDto(entity.getArticle().getUser()));
        }

        if (entity.getArticle().getBoard() != null) {
            dto.setBoard(BoardMapper.toDto(entity.getArticle().getBoard()));
        }

        if (!entity.getArticle().getCommentList().isEmpty()) {
            List<Comment> commentList = entity.getArticle().getCommentList();
            List<CommentDto> commentDtoList = new ArrayList<>();
            for (Comment comment : commentList) {
                CommentDto commentDto = CommentMapper.toDto(comment);
                commentDtoList.add(commentDto);
            }
            dto.setCommentList(commentDtoList);
        }

        if (!entity.getArticle().getAttachmentList().isEmpty()) {
            List<Attachment> attachmentList = entity.getArticle().getAttachmentList();
            List<AttachmentDto> attachmentDtoList = new ArrayList<>();
            for (Attachment attachment : attachmentList) {
                AttachmentDto attachmentDto = AttachmentMapper.toDto(attachment);
                attachmentDto.setArticle(null);
                attachmentDtoList.add(attachmentDto);
            }
            dto.setAttachmentList(attachmentDtoList);
        }

        return dto;
    }

    public static MarketResponseDto toResponse(MarketDto dto) {
        MarketResponseDto responseDto = MarketResponseDto.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .body(dto.getBody())
                .view(dto.getView())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .price(dto.getPrice())
                .state(dto.getState())
                .tag(dto.getTag())
                .build();

        responseDto.setFiles(dto.getAttachmentList()
                .stream().map(attachment -> AttachmentMapper.toResponse(attachment)).toList());

        if (dto.getUser() != null) {
            responseDto.setUser(UserMapper.toResponse(dto.getUser()));
        }

        if (dto.getBoard() != null) {
            responseDto.setBoard(BoardMapper.toResponse(dto.getBoard()));
        }

        if (!dto.getCommentList().isEmpty()) {
            List<CommentDto> commentList = dto.getCommentList();
            var commentResponseDtoList = commentList.stream().map(comment -> CommentMapper.toResponse(comment)).toList();
            responseDto.setComments(commentResponseDtoList);
        }

        return responseDto;
    }
}
