package com.charmroom.charmroom.apicontroller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.charmroom.charmroom.dto.CommonResponseDto;
import com.charmroom.charmroom.dto.SignupDto.SignupRequestDto;
import com.charmroom.charmroom.dto.SignupDto.SignupResponseDto;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthApiController {
	private final UserService userService;
	
	@PostMapping("/signup")
	public ResponseEntity<?> signup(
			@RequestBody @Valid SignupRequestDto signupRequestDto,
			BindingResult bindingResult) {
		if (!signupRequestDto.getPassword().equals(signupRequestDto.getRePassword())) {
			bindingResult.rejectValue("rePassword", "passwordIncorrect",
					"패스워드 확인이 일치하지 않습니다.");
		}
		
		if (bindingResult.hasErrors()) {
			Map<String, String> errors = new HashMap<>();
			bindingResult.getFieldErrors().forEach(error -> {
				errors.put(error.getField(), error.getDefaultMessage());
			});
			return ResponseEntity.badRequest().body(CommonResponseDto.invalid(errors));
		}
		
		User created = userService.create(signupRequestDto.getUsername(), signupRequestDto.getEmail(),
				signupRequestDto.getNickname(), signupRequestDto.getPassword());
		
		return ResponseEntity.ok(CommonResponseDto.okay(SignupResponseDto.fromEntity(created)));
	}
}
