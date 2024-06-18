package com.charmroom.charmroom.controller.advice;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.charmroom.charmroom.dto.CommonResponseDto;

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
		return ResponseEntity.badRequest().body(CommonResponseDto.invalid(errors));
	}
}
