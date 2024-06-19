package com.charmroom.charmroom.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CustomError {
	UNKNOWN(HttpStatus.BAD_REQUEST, "000", "Unknown"), 
	DUPLICATED_USERNAME(HttpStatus.BAD_REQUEST, "", "Duplicated username"),
	DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "", "Duplicated email"),
	DUPLICATED_NICKNAME(HttpStatus.BAD_REQUEST, "", "Duplicated nickname"),
	
	
	USER_NOTFOUND(HttpStatus.NOT_FOUND, "", "User not found")
	;
	private final HttpStatus status;
	private final String code;
	private final String describe;
}
