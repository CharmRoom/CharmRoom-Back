package com.charmroom.charmroom.dto.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class TokenDto {
	private String access;
	private String refresh;
}
