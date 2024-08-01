package com.charmroom.charmroom.controller.api;

import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.charmroom.charmroom.dto.business.ArticleMapper;
import com.charmroom.charmroom.dto.business.BoardMapper;
import com.charmroom.charmroom.dto.presentation.CommonResponseDto;
import com.charmroom.charmroom.service.BoardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
@PreAuthorize("permitAll()")
public class BoardController {
	private final BoardService boardService;
	
	@GetMapping
	public ResponseEntity<?> getBoardsExposed(){
		var dtos = boardService.getBoardsExposed();
		var response = dtos.stream().map(BoardMapper::toResponse).toList();
		return CommonResponseDto.ok(response).toResponseEntity();
	}
	
	@GetMapping("/all")
	public ResponseEntity<?> getAllBoards(
			@PageableDefault(size=10, sort="id", direction=Sort.Direction.DESC) Pageable pageable
			){
		var dtos = boardService.getBoards(pageable);
		var response = dtos.map(BoardMapper::toResponse);
		return CommonResponseDto.ok(response).toResponseEntity();
	}

	
	@GetMapping("/{boardId}")
	public ResponseEntity<?> getArticles(
			@PathVariable("boardId") Integer boardId,
			@PageableDefault(size=10, sort="id", direction=Sort.Direction.DESC) Pageable pageable
			){
		var dtos = boardService.getArticlesByBoardId(boardId, pageable);
		var response = dtos.map(ArticleMapper::toResponse);
		return CommonResponseDto.ok(response).toResponseEntity();
	}
}
