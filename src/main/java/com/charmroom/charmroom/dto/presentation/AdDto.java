package com.charmroom.charmroom.dto.presentation;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public class AdDto {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class AdCreateRequestDto {
        @NotBlank
        private String title;
        @NotBlank
        @Size(min = 255)
        private String link;
        @NotNull
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime start;
        @NotNull
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime end;
        @NotNull
        private MultipartFile image;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class AdUpdateRequestDto {
        @NotBlank
        private String title;
        @NotBlank
        @Size(max = 255)
        private String link;
        @NotNull
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime start;
        @NotNull
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime end;
        @NotNull
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
