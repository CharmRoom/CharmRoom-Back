package com.charmroom.charmroom.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.charmroom.charmroom.dto.business.UserDto;
import com.charmroom.charmroom.dto.business.UserMapper;
import com.charmroom.charmroom.dto.presentation.CommonResponseDto;
import com.charmroom.charmroom.dto.presentation.UserDto.SignupRequestDto;
import com.charmroom.charmroom.dto.presentation.UserDto.UserResponseDto;
import com.charmroom.charmroom.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@PreAuthorize("permitAll()")
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
		UserDto created = userService.create(userDto, signupRequestDto.getImage());
		UserResponseDto result = UserMapper.toResponse(created);
		return CommonResponseDto.created(result).toResponseEntity();

	}
}
