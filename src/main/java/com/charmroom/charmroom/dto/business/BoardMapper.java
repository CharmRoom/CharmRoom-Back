package com.charmroom.charmroom.dto.business;

import com.charmroom.charmroom.entity.Board;

public class BoardMapper {
	public static BoardDto toDto(Board entity) {
		return BoardDto.builder()
				.id(entity.getId())
				.name(entity.getName())
				.type(entity.getType())
				.exposed(entity.getExposed())
				.build();
	}
}
