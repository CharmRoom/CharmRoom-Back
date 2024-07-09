package com.charmroom.charmroom.dto.presentation;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public class AdDto {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class AdCreateRequestDto {
        private String title;
        private String link;
        private LocalDateTime start;
        private LocalDateTime end;
        private MultipartFile image;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class AdUpdateRequestDto {
        private String title;
        private String link;
        private LocalDateTime start;
        private LocalDateTime end;
        private MultipartFile image;
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class AdResponseDto {
        private Integer id;
        private String title;
        private String link;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime start;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime end;
        private ImageDto.ImageResponseDto image;
    }
}
