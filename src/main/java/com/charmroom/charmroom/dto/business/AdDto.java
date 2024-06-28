package com.charmroom.charmroom.dto.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class AdDto {
    private Integer id;
    private String title;
    private LocalDateTime start;
    private LocalDateTime end;
    private String link;
    private ImageDto image;
}
