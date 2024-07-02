package com.charmroom.charmroom.controller.api;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.charmroom.charmroom.dto.business.UserMapper;
import com.charmroom.charmroom.dto.presentation.CommonResponseDto;
import com.charmroom.charmroom.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
	private final UserService userService;
	
	@GetMapping("/user")
	public ResponseEntity<?> users(
			@PageableDefault(size=10, sort="id", direction=Sort.Direction.DESC) Pageable pageable){
		var dtos = userService.getAllUsersByPageable(pageable);
		var response = dtos.map(dto -> UserMapper.toResponse(dto));
		return CommonResponseDto.ok(response).toResponseEntity();
	}
	
	@PatchMapping("/user/grade")
	public ResponseEntity<?> changeUserGrade(
			@RequestParam(name = "username") String username,
			@RequestParam(name = "grade") String grade
			){
		var dto = userService.changeLevel(username, grade);
		var response = UserMapper.toResponse(dto);
		return CommonResponseDto.ok(response).toResponseEntity();
	}
	
	@PatchMapping("/user/withdraw")
	public ResponseEntity<?> changeUserWithdraw(
			@RequestParam(name = "username") String username,
			@RequestParam(name = "withdraw", defaultValue = "true") Boolean withdraw
			){
		var dto = userService.changeWithdraw(username, withdraw);
		var response = UserMapper.toResponse(dto);
		return CommonResponseDto.ok(response).toResponseEntity();
	}
}
