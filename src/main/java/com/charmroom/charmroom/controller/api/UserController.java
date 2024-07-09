package com.charmroom.charmroom.controller.api;

import com.charmroom.charmroom.dto.business.ArticleDto;
import com.charmroom.charmroom.dto.business.ArticleMapper;
import com.charmroom.charmroom.dto.business.CommentMapper;
import com.charmroom.charmroom.dto.business.PointMapper;
import com.charmroom.charmroom.dto.business.SubscribeDto;
import com.charmroom.charmroom.dto.business.SubscribeMapper;
import com.charmroom.charmroom.dto.presentation.SubscribeDto.SubscribeCreateRequestDto;
import com.charmroom.charmroom.dto.presentation.ArticleDto.ArticleResponseDto;
import com.charmroom.charmroom.dto.business.UserMapper;
import com.charmroom.charmroom.dto.presentation.CommonResponseDto;
import com.charmroom.charmroom.dto.presentation.SubscribeDto.SubscribeResponseDto;
import com.charmroom.charmroom.dto.presentation.UserDto.UserUpdateRequest;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.service.ArticleService;
import com.charmroom.charmroom.service.CommentService;
import com.charmroom.charmroom.service.PointService;
import com.charmroom.charmroom.service.SubscribeService;
import com.charmroom.charmroom.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	private final PointService pointService;
	private final CommentService commentService;
	private final SubscribeService subscribeService;
	private final ArticleService articleService;

	@GetMapping("")
	public ResponseEntity<?> getMyInfo(@AuthenticationPrincipal User user) {
		var dto = userService.getUserByUsername(user.getUsername());
		var response = UserMapper.toResponse(dto);
		return CommonResponseDto.ok(response).toResponseEntity();
	}

	@PatchMapping("")
	public ResponseEntity<?> updateMyInfo(
			@AuthenticationPrincipal User user,
			@RequestBody @Valid UserUpdateRequest request) {
		var dto = userService.changeNickname(user.getUsername(), request.getNickname());
		var response = UserMapper.toResponse(dto);
		return CommonResponseDto.ok(response).toResponseEntity();
	}

	@PatchMapping("/withdraw")
	public ResponseEntity<?> withdraw(
			@AuthenticationPrincipal User user
	) {
		var dto = userService.changeWithdraw(user.getUsername(), true);
		var response = UserMapper.toResponse(dto);
		return CommonResponseDto.ok(response).toResponseEntity();
	}

	@GetMapping("/point")
	public ResponseEntity<?> getMyPoints(
			@AuthenticationPrincipal User user,
			@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
		var dtos = pointService.pointsByUsername(user.getUsername(), pageable);
		var response = dtos.map(dto -> PointMapper.toResponse(dto));
		return CommonResponseDto.ok(response).toResponseEntity();
	}

	@GetMapping("/comment")
	public ResponseEntity<?> getMyComments(
			@AuthenticationPrincipal User user,
			@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
	) {
		var dtos = commentService.getCommentsByUsername(user.getUsername(), pageable);
		var response = dtos.map(dto -> CommentMapper.toResponse(dto));
		return CommonResponseDto.ok(response).toResponseEntity();
	}

	@GetMapping("/article")
	public ResponseEntity<?> getMyArticles(
			@AuthenticationPrincipal User user,
			@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
			) {
		Page<ArticleDto> dtos = articleService.getArticlesByUsername(user.getUsername(), pageable);
		Page<ArticleResponseDto> response = dtos.map(dto -> ArticleMapper.toResponse(dto));

		return CommonResponseDto.ok(response).toResponseEntity();
	}

	@PostMapping("")
	public ResponseEntity<?> subscribe(
			@RequestBody SubscribeCreateRequestDto request
	) {
		SubscribeDto dto = subscribeService.subscribeOrCancel(request.getSubscriberUserName(), request.getTargetUserName());
		SubscribeResponseDto response = SubscribeMapper.toResponse(dto);
		return CommonResponseDto.ok(response).toResponseEntity();
	}


	@GetMapping("/subscribe")
	public ResponseEntity<?> getMySubscribes(
			@AuthenticationPrincipal User user,
			@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
	) {
		Page<SubscribeDto> dtos = subscribeService.getSubscribesBySubscriber(user.getUsername(), pageable);
		Page<SubscribeResponseDto> response = dtos.map(dto -> SubscribeMapper.toResponse(dto));

		return CommonResponseDto.ok(response).toResponseEntity();
	}


}
