package com.charmroom.charmroom.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomException extends RuntimeException {

	private static final long serialVersionUID = -614862637671269886L;

	private final CustomError error;
	private final String message;
	
	public CustomException(CustomError error) {
		this.error = error;
		this.message = "";
	}
	
	public CustomException(CustomError error, Throwable cause) {
		this.error = error;
		this.message = cause.getMessage();
	}
	
	public CustomException(Exception exception) {
		if (exception.getClass() == CustomException.class) {
			CustomException customException = (CustomException) exception;
			this.error = customException.getError();
			this.message = customException.getMessage();
		} else {
			this.error = CustomError.UNKNOWN;
			this.message = exception.getMessage();
		}
	}
}
