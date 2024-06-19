package com.charmroom.charmroom.dto;

import org.springframework.http.ResponseEntity;

import com.charmroom.charmroom.exception.CustomException;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomErrorResponseDto {
	private String code;
	private String describe;
	private String message;
	
	public static ResponseEntity<?> toResponse(CustomException e){
		return ResponseEntity
				.status(e.getError().getStatus())
				.body(
						CustomErrorResponseDto.builder()
						.code(e.getError().getCode())
						.describe(e.getError().getDescribe())
						.message(e.getMessage())
						.build()
						);
	}
}
