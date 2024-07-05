package com.charmroom.charmroom.controller.api;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.charmroom.charmroom.dto.business.CommentMapper;
import com.charmroom.charmroom.dto.business.PointMapper;
import com.charmroom.charmroom.dto.business.UserMapper;
import com.charmroom.charmroom.dto.presentation.CommonResponseDto;
import com.charmroom.charmroom.dto.presentation.UserDto.UserUpdateRequest;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.service.CommentService;
import com.charmroom.charmroom.service.PointService;
import com.charmroom.charmroom.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	private final PointService pointService;
	private final CommentService commentService;
	
	@GetMapping("")
	public ResponseEntity<?> getMyInfo(@AuthenticationPrincipal User user){
		var dto = userService.getUserByUsername(user.getUsername());
		var response = UserMapper.toResponse(dto);
		return CommonResponseDto.ok(response).toResponseEntity();
	}
	
	@PatchMapping("")
	public ResponseEntity<?> updateMyInfo(
			@AuthenticationPrincipal User user,
			@RequestBody UserUpdateRequest request){
		var dto = userService.changeNickname(user.getUsername(), request.getNickname());
		var response = UserMapper.toResponse(dto);
		return CommonResponseDto.ok(response).toResponseEntity();
	}
	
	@PatchMapping("/withdraw")
	public ResponseEntity<?> withdraw(
			@AuthenticationPrincipal User user
			){
		var dto = userService.changeWithdraw(user.getUsername(), true);
		var response = UserMapper.toResponse(dto);
		return CommonResponseDto.ok(response).toResponseEntity();
	}
		
	@GetMapping("/point")
	public ResponseEntity<?> getMyPoints(
			@AuthenticationPrincipal User user,
			@PageableDefault(size=10, sort="id", direction=Sort.Direction.DESC) Pageable pageable){
		var dtos = pointService.pointsByUsername(user.getUsername(), pageable);
		var response = dtos.map(dto -> PointMapper.toResponse(dto));
		return CommonResponseDto.ok(response).toResponseEntity();
	}
	
	@GetMapping("/comment")
	public ResponseEntity<?> getMyComments(
			@AuthenticationPrincipal User user,
			@PageableDefault(size=10, sort="id", direction=Sort.Direction.DESC) Pageable pageable
			){
		var dtos = commentService.getCommentsByUsername(user.getUsername(), pageable);
		var response = dtos.map(dto -> CommentMapper.toResponse(dto));
		return CommonResponseDto.ok(response).toResponseEntity();
	}
	
}
