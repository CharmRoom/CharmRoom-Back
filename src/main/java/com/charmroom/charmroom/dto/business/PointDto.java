package com.charmroom.charmroom.dto.business;

import java.time.LocalDateTime;

import com.charmroom.charmroom.entity.enums.PointType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PointDto {
	private Integer id;
	private UserDto user;
	private LocalDateTime updatedAt;
	private PointType type;
	private Integer diff;
}
