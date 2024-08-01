package com.charmroom.charmroom.controller.api;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.charmroom.charmroom.dto.business.CommentDto;
import com.charmroom.charmroom.dto.business.CommentLikeMapper;
import com.charmroom.charmroom.dto.business.CommentMapper;
import com.charmroom.charmroom.dto.presentation.CommentDto.CommentCreateRequestDto;
import com.charmroom.charmroom.dto.presentation.CommentDto.CommentUpdateRequestDto;
import com.charmroom.charmroom.dto.presentation.CommonResponseDto;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.service.CommentLikeService;
import com.charmroom.charmroom.service.CommentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {
	private final CommentService commentService;
	private final CommentLikeService commentLikeService;
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/{articleId}")
	public ResponseEntity<?> create(
			@AuthenticationPrincipal User user,
			@PathVariable("articleId") Integer articleId,
			@RequestBody CommentCreateRequestDto request
			){
		
		CommentDto dto;
		if (request.getParentId() != null)
			dto = commentService.create(articleId, user.getUsername(), request.getBody(), request.getParentId());
		else
			dto = commentService.create(articleId, user.getUsername(), request.getBody());
		
		var response = CommentMapper.toResponse(dto);
		return CommonResponseDto.created(response).toResponseEntity();
	}
	
	@PreAuthorize("permitAll()")
	@GetMapping("/{articleId}")
	public ResponseEntity<?> getCommentList(
			@PathVariable("articleId") Integer articleId,
			@PageableDefault(size=10, sort="id", direction=Sort.Direction.DESC) Pageable pageable
			){
		var dtos = commentService.getComments(articleId, pageable);
		var response = dtos.map(CommentMapper::toResponse);
		return CommonResponseDto.ok(response).toResponseEntity();
	}
	
	@PreAuthorize("isAuthenticated()")
	@PatchMapping("/{commentId}")
	public ResponseEntity<?> update(
			@AuthenticationPrincipal User user,
			@PathVariable("commentId") Integer commentId,
			@RequestBody CommentUpdateRequestDto request
			){
		var dto = commentService.update(commentId, user.getUsername(), request.getBody());
		var response = CommentMapper.toResponse(dto);
		return CommonResponseDto.ok(response).toResponseEntity();
	}
	
	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("/{commentId}")
	public ResponseEntity<?> disable(
			@AuthenticationPrincipal User user,
			@PathVariable("commentId") Integer commentId
			){
		var dto = commentService.disable(commentId, user.getUsername());
		var response = CommentMapper.toResponse(dto);
		return CommonResponseDto.ok(response).toResponseEntity();
	}
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/like/{commentId}")
	public ResponseEntity<?> like(
			@AuthenticationPrincipal User user,
			@PathVariable("commentId") Integer commentId
			){
		var dto = commentLikeService.like(user.getUsername(), commentId);
		var response = CommentLikeMapper.toResponse(dto);
		return CommonResponseDto.ok(response).toResponseEntity();
	}
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/dislike/{commentId}")
	public ResponseEntity<?> dislike(
			@AuthenticationPrincipal User user,
			@PathVariable("commentId") Integer commentId
			){
		var dto = commentLikeService.dislike(user.getUsername(), commentId);
		var response = CommentLikeMapper.toResponse(dto);
		return CommonResponseDto.ok(response).toResponseEntity();
	}
}
