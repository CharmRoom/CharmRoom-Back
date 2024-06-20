package com.charmroom.charmroom.dto;

import org.springframework.http.ResponseEntity;

import com.charmroom.charmroom.exception.BusinessLogicException;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BusinessLogicErrorResponseDto {
	private String code;
	private String describe;
	private String message;
	
	public static ResponseEntity<?> toResponse(BusinessLogicException e){
		return ResponseEntity
				.status(e.getError().getStatus())
				.body(
						BusinessLogicErrorResponseDto.builder()
						.code(e.getError().getCode())
						.describe(e.getError().getDescribe())
						.message(e.getMessage())
						.build()
						);
	}
}
