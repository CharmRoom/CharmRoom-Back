package com.charmroom.charmroom.dto.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PointTypeValidator.class)
public @interface ValidPointType {
	String message() default "Invalid point type";
	Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
