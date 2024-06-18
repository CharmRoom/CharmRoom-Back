package com.charmroom.charmroom.dto.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;


public @interface ValidUser {
	
	public @interface Same{
		@Target(ElementType.TYPE)
		@Retention(RetentionPolicy.RUNTIME)
		@Constraint(validatedBy = UserValidator.SameValidator.PasswordConfirmValidator.class)
		public @interface PasswordConfirm{
			String message() default "Different password and rePassword";
			Class<?>[] groups() default {};
		    Class<? extends Payload>[] payload() default {};
		    String password() default "password";
		    String rePassword() default "rePassword";
		}
	}
	public @interface Unique{
		@Target(ElementType.FIELD)
		@Retention(RetentionPolicy.RUNTIME)
		@Constraint(validatedBy = UserValidator.UniqueValidator.UsernameValidator.class)
		public @interface Username {
			String message() default "Duplicated username";
			Class<?>[] groups() default {};
		    Class<? extends Payload>[] payload() default {};
		}
		
		@Target(ElementType.FIELD)
		@Retention(RetentionPolicy.RUNTIME)
		@Constraint(validatedBy = UserValidator.UniqueValidator.EmailValidator.class)
		public @interface Email {
			String message() default "Duplicated e-mail";
			Class<?>[] groups() default {};
		    Class<? extends Payload>[] payload() default {};
		}
		
		@Target(ElementType.FIELD)
		@Retention(RetentionPolicy.RUNTIME)
		@Constraint(validatedBy = UserValidator.UniqueValidator.NicknameValidator.class)
		public @interface Nickname {
			String message() default "Duplicated nickname";
			Class<?>[] groups() default {};
		    Class<? extends Payload>[] payload() default {};
		}
	}
}
