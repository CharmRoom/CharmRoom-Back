package com.charmroom.charmroom.dto.validation;

import org.springframework.stereotype.Component;

import com.charmroom.charmroom.repository.UserRepository;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@Component
public class UserValidator {
	
	@Component
	public class SameValidator{
		@Component
		public class PasswordConfirmValidator implements 
		ConstraintValidator<ValidUser.Same.PasswordConfirm, Object> {
			private String password;
			private String rePassword;
			
			@Override
			public void initialize(ValidUser.Same.PasswordConfirm annotation) {
				password = annotation.password();
				rePassword = annotation.rePassword();
			}
			@Override
			public boolean isValid(Object object, ConstraintValidatorContext context) {
				String passwordValue = getFieldValue(object, password);
				String rePasswordValue = getFieldValue(object, rePassword);
				return passwordValue.equals(rePasswordValue);
			}
			
			private String getFieldValue(Object object, String fieldName) {
				Class<?> clazz = object.getClass();
				String value = null;
				try {
					var field = clazz.getDeclaredField(fieldName);
					field.setAccessible(true);
					var target = field.get(object);
					if (target instanceof String) {
						value = new String((String) target);
						field.setAccessible(false);
					} else {
						throw new ClassCastException("Field: " + fieldName + "is not string");
					}
				} catch (NoSuchFieldException e) {
					System.out.println("No Such Field: " + fieldName);
				} catch(IllegalAccessException e) {
					System.out.println("Illegal Access Field: " + fieldName);
				}
				return value;
			}
		}
	}
	
	@Component
	@RequiredArgsConstructor
	public class UniqueValidator{
		private final UserRepository userRepository;
		public class UsernameValidator implements ConstraintValidator<ValidUser.Unique.Username, String>{
			@Override
			public boolean isValid(String value, ConstraintValidatorContext context) {
				return !userRepository.existsByUsername(value);
			}
		}
		
		public class EmailValidator implements ConstraintValidator<ValidUser.Unique.Email, String>{
			@Override
			public boolean isValid(String value, ConstraintValidatorContext context) {
				return !userRepository.existsByEmail(value);
			}
		}
		
		public class NicknameValidator implements ConstraintValidator<ValidUser.Unique.Nickname, String>{
			@Override
			public boolean isValid(String value, ConstraintValidatorContext context) {
				return !userRepository.existsByNickname(value);
			}
		}
	}
	
	
}
