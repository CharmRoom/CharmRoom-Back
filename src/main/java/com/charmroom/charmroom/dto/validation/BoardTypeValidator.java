package com.charmroom.charmroom.dto.validation;

import java.util.List;

import com.charmroom.charmroom.entity.enums.BoardType;

import io.jsonwebtoken.lang.Arrays;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BoardTypeValidator implements ConstraintValidator<ValidBoardType, String>{
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		var types = Arrays.asList(BoardType.values());
		List<String> values = types.stream().map(v -> v.toString()).toList();
		return values.contains(value);
	}
}
