package com.charmroom.charmroom.dto.presentation;

import jakarta.validation.constraints.NotBlank;
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
        @NotBlank
        private String name;
        @NotBlank
        private String description;
        @NotBlank
        private String contact;
        private MultipartFile image;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ClubUpdateRequestDto {
        @NotBlank
        private String name;
        @NotBlank
        private String description;
        @NotBlank
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
        private UserDto.UserResponseDto owner;
        private ImageDto.ImageResponseDto image;
    }
}
