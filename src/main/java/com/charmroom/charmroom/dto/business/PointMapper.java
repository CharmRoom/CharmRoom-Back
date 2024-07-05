package com.charmroom.charmroom.dto.business;

import java.util.List;

import com.charmroom.charmroom.dto.presentation.PointDto.PointResponseDto;
import com.charmroom.charmroom.entity.Point;

import io.jsonwebtoken.lang.Arrays;

public class PointMapper {
	public static PointDto toDto(Point entity, String... ignore) {
		if (entity == null) return null;
		List<String> ignores = Arrays.asList(ignore);
		PointDto dto =  PointDto.builder()
				.id(entity.getId())
				.updatedAt(entity.getUpdatedAt())
				.type(entity.getType())
				.diff(entity.getDiff())
				.build();
		
		if (entity.getUser() != null && !ignores.contains("user")) {
			UserDto user = UserMapper.toDto(entity.getUser(), "pointList");
			dto.setUser(user);
		}
		return dto;
	}
	
	public static PointResponseDto toResponse(PointDto dto) {
		var response = PointResponseDto.builder()
				.id(dto.getId())
				.updatedAt(dto.getUpdatedAt())
				.type(dto.getType().name())
				.diff(dto.getDiff())
				.build();
		if (dto.getUser() != null)
			response.setUsername(dto.getUser().getUsername());
		return response;
	}
}
