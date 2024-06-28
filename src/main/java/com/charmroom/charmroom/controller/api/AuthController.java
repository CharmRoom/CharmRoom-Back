package com.charmroom.charmroom.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.charmroom.charmroom.dto.business.UserDto;
import com.charmroom.charmroom.dto.presentation.CommonResponseDto;
import com.charmroom.charmroom.dto.presentation.SignupDto.SignupRequestDto;
import com.charmroom.charmroom.dto.presentation.SignupDto.SignupResponseDto;
import com.charmroom.charmroom.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	private final UserService userService;
	
	@PostMapping("/signup")
	public ResponseEntity<?> signup(
			@ModelAttribute @Valid SignupRequestDto signupRequestDto) {
		UserDto userDto = UserDto.builder()
				.username(signupRequestDto.getUsername())
				.email(signupRequestDto.getEmail())
				.nickname(signupRequestDto.getNickname())
				.password(signupRequestDto.getPassword())
				.build();
		UserDto created = userService.create(userDto);
		SignupResponseDto result = SignupResponseDto.builder()
				.username(created.getUsername())
				.email(created.getEmail())
				.nickname(created.getNickname())
				.role(created.getLevel().getValue())
				.build();
		return CommonResponseDto.ok(result).toResponse();

	}
}
