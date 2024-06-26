package com.charmroom.charmroom.dto.business;

import com.charmroom.charmroom.entity.Point;

public class PointMapper {
	public static PointDto toDto(Point entity) {
		if (entity == null) return null;
		
		PointDto dto =  PointDto.builder()
				.id(entity.getId())
				.updatedAt(entity.getUpdatedAt())
				.type(entity.getType())
				.diff(entity.getDiff())
				.build();
		
		if (entity.getUser() != null) {
			UserDto user = UserMapper.toDto(entity.getUser());
			user.setPointList(null);
			dto.setUser(user);
		}
		return dto;
	}
}
