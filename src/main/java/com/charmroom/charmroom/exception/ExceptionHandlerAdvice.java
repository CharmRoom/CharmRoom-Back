package com.charmroom.charmroom.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.charmroom.charmroom.dto.CommonResponseDto;
import com.charmroom.charmroom.dto.CustomErrorResponseDto;

@RestControllerAdvice(annotations = RestController.class)
public class ExceptionHandlerAdvice {
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException e){
		Map<String, String> errors = new HashMap<>();
		e.getBindingResult().getAllErrors().forEach((err) -> {
			String key = err.getObjectName();
			if (err instanceof FieldError) 
				key = ((FieldError)err).getField();
			errors.put(key, err.getDefaultMessage());
		});
		return CommonResponseDto.invalid(errors).toResponse();
	}
	
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<?> handleCustomException(CustomException e){
		return CustomErrorResponseDto.toResponse(e);
	}
}
