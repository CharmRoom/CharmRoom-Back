package com.charmroom.charmroom.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BusinessLogicException extends RuntimeException {

	private static final long serialVersionUID = -614862637671269886L;

	private final BusinessLogicError error;
	private final String message;
	
	public BusinessLogicException(BusinessLogicError error) {
		this.error = error;
		this.message = "";
	}
	
	public BusinessLogicException(BusinessLogicError error, Throwable cause) {
		this.error = error;
		this.message = cause.getMessage();
	}
	
	public BusinessLogicException(Exception exception) {
		if (exception.getClass() == BusinessLogicException.class) {
			BusinessLogicException customException = (BusinessLogicException) exception;
			this.error = customException.getError();
			this.message = customException.getMessage();
		} else {
			this.error = BusinessLogicError.UNKNOWN;
			this.message = exception.getMessage();
		}
	}
}
