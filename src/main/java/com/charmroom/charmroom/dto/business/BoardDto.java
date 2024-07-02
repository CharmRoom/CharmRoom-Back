package com.charmroom.charmroom.dto.business;

import com.charmroom.charmroom.entity.enums.BoardType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class BoardDto {
	private Integer id;
	private String name;
	private BoardType type;
	private boolean exposed;
}
