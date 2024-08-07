package com.charmroom.charmroom.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.charmroom.charmroom.dto.business.TokenDto;
import com.charmroom.charmroom.dto.business.UserDto;
import com.charmroom.charmroom.dto.business.UserMapper;
import com.charmroom.charmroom.dto.presentation.CommonResponseDto;
import com.charmroom.charmroom.dto.presentation.UserDto.CreateUserRequestDto;
import com.charmroom.charmroom.dto.presentation.UserDto.UserResponseDto;
import com.charmroom.charmroom.service.AuthService;
import com.charmroom.charmroom.service.UserService;

import io.jsonwebtoken.lang.Arrays;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@PreAuthorize("permitAll()")
public class AuthController {
	private final UserService userService;
	private final AuthService authService;
	
	@PostMapping("/signup")
	public ResponseEntity<?> signup(
			@ModelAttribute @Valid CreateUserRequestDto createUserRequestDto) {
		UserDto userDto = UserMapper.toBusinessDto(createUserRequestDto);
		UserDto created = userService.create(userDto, createUserRequestDto.getImage());
		UserResponseDto result = UserMapper.toResponse(created);
		return CommonResponseDto.created(result).toResponseEntity();
	}
	
	@PostMapping("/reissue")
	public ResponseEntity<?> reissue(
			HttpServletRequest request,
			HttpServletResponse response
			){
		String refreshToken = null;
		
		var cookies = Arrays.asList(request.getCookies());
		for(Cookie cookie: cookies) {
			if (cookie.getName().equals("refresh")) {
				refreshToken = cookie.getValue();
				break;
			}
		}
		TokenDto token = authService.reissue(refreshToken);
		Cookie refresh = new Cookie("refresh", token.getRefresh());
		refresh.setMaxAge(24*60*60);
		refresh.setHttpOnly(true);
		
		response.setHeader("Authorization", "Bearer " + token.getAccess());
		response.addCookie(refresh);
		
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/logout")
	public ResponseEntity<?> logout(
			HttpServletRequest request,
			HttpServletResponse response
			){
		String refreshToken = null;
		var cookies = Arrays.asList(request.getCookies());
		for(Cookie cookie: cookies) {
			if (cookie.getName().equals("refresh")) {
				refreshToken = cookie.getValue();
				break;
			}
		}
		authService.logout(refreshToken);
		Cookie refresh = new Cookie("refresh", null);
		refresh.setMaxAge(0);
		refresh.setHttpOnly(true);
		response.addCookie(refresh);
		return ResponseEntity.ok().build();
	}
}
