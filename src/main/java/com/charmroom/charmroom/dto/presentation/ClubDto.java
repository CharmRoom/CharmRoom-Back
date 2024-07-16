package com.charmroom.charmroom.dto.presentation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

public class ClubDto {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ClubCreateRequestDto {
        private String name;
        private String description;
        private String contact;
        private MultipartFile image;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ClubUpdateRequestDto {
        private String name;
        private String description;
        private String contact;
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class ClubResponseDto {
        private Integer id;
        private String name;
        private String description;
        private String contact;
        private ImageDto.ImageResponseDto image;
    }
}
