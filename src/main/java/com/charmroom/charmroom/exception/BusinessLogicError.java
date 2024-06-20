package com.charmroom.charmroom.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BusinessLogicError {
	UNKNOWN(HttpStatus.BAD_REQUEST, "00000", "Unknown"), 
	DUPLICATED_USERNAME(HttpStatus.BAD_REQUEST, "12000", "Duplicated username"),
	DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "12001", "Duplicated email"),
	DUPLICATED_NICKNAME(HttpStatus.BAD_REQUEST, "12002", "Duplicated nickname"),
	
	NOTFOUND_USER(HttpStatus.NOT_FOUND, "12100", "User not found")
	;
	private final HttpStatus status;
	private final String code;
	private final String describe;
}
