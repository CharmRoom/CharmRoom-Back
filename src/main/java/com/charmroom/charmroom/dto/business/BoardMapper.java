package com.charmroom.charmroom.dto.business;

import com.charmroom.charmroom.dto.presentation.BoardDto.BoardResponseDto;
import com.charmroom.charmroom.entity.Board;

public class BoardMapper {
	public static BoardDto toDto(Board entity) {
		return BoardDto.builder()
				.id(entity.getId())
				.name(entity.getName())
				.type(entity.getType())
				.exposed(entity.isExposed())
				.build();
	}

	public static BoardResponseDto toResponse(BoardDto dto) {
		return BoardResponseDto.builder()
				.id(dto.getId())
				.name(dto.getName())
				.type(dto.getType().toString())
				.exposed(dto.isExposed())
				.build();
	}
}
