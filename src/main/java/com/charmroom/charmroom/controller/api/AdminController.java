package com.charmroom.charmroom.controller.api;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.charmroom.charmroom.dto.business.BoardMapper;
import com.charmroom.charmroom.dto.business.PointMapper;
import com.charmroom.charmroom.dto.business.UserMapper;
import com.charmroom.charmroom.dto.presentation.BoardDto.BoardCreateRequestDto;
import com.charmroom.charmroom.dto.presentation.BoardDto.BoardUpdateRequestDto;
import com.charmroom.charmroom.dto.presentation.CommonResponseDto;
import com.charmroom.charmroom.dto.presentation.PointDto.PointCreateRequestDto;
import com.charmroom.charmroom.service.BoardService;
import com.charmroom.charmroom.service.PointService;
import com.charmroom.charmroom.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
	private final UserService userService;
	private final BoardService boardService;
	private final PointService pointService;
	
	@GetMapping("/user")
	public ResponseEntity<?> users(
			@PageableDefault(size=10, sort="id", direction=Sort.Direction.DESC) Pageable pageable){
		var dtos = userService.getAllUsersByPageable(pageable);
		var response = dtos.map(dto -> UserMapper.toResponse(dto));
		return CommonResponseDto.ok(response).toResponseEntity();
	}
	
	@PatchMapping("/user/grade")
	public ResponseEntity<?> changeUserGrade(
			@RequestParam("username") String username,
			@RequestParam("grade") String grade
			){
		var dto = userService.changeLevel(username, grade);
		var response = UserMapper.toResponse(dto);
		return CommonResponseDto.ok(response).toResponseEntity();
	}
	
	@PatchMapping("/user/withdraw")
	public ResponseEntity<?> changeUserWithdraw(
			@RequestParam("username") String username,
			@RequestParam(name = "withdraw", defaultValue = "true") Boolean withdraw
			){
		var dto = userService.changeWithdraw(username, withdraw);
		var response = UserMapper.toResponse(dto);
		return CommonResponseDto.ok(response).toResponseEntity();
	}
	
	@PostMapping("/point/{username}")
	public ResponseEntity<?> givePoint(
			@PathVariable("username") String username,
			@RequestBody PointCreateRequestDto request
			){
		var dto = pointService.create(username, request.getType(), request.getDiff());
		var response = PointMapper.toResponse(dto);
		return CommonResponseDto.created(response).toResponseEntity();
		
	}
	@PostMapping("/board")
	public ResponseEntity<?> createBoard(
			@RequestBody BoardCreateRequestDto request
			){
		var dto = boardService.create(request.getName(), request.getType());
		var response = BoardMapper.toResponse(dto);
		return CommonResponseDto.created(response).toResponseEntity();
	}
	
	@PostMapping("/board/{boardId}")
	public ResponseEntity<?> updateBoard(
			@PathVariable("boardId") Integer boardId,
			@RequestBody BoardUpdateRequestDto request
			){
		var dto = boardService.update(boardId, request.getName(), request.getType());
		var response = BoardMapper.toResponse(dto);
		return CommonResponseDto.ok(response).toResponseEntity();
	}
	
	@PatchMapping("/board/{boardId}")
	public ResponseEntity<?> exposeBoard(
			@PathVariable("boardId") Integer boardId,
			@RequestParam("exposed") Boolean exposed 
			) {
		var dto = boardService.changeExpose(boardId, exposed);
		var response = BoardMapper.toResponse(dto);
		return CommonResponseDto.ok(response).toResponseEntity();
	}
	
	@DeleteMapping("/board/{boardId}")
	public ResponseEntity<?> deleteBoard(
			@PathVariable("boardId") Integer boardId
			){
		boardService.delete(boardId);
		return CommonResponseDto.ok().toResponseEntity();
	}
	
}
