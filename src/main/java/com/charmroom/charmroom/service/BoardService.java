package com.charmroom.charmroom.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.charmroom.charmroom.dto.business.ArticleDto;
import com.charmroom.charmroom.dto.business.ArticleMapper;
import com.charmroom.charmroom.dto.business.BoardDto;
import com.charmroom.charmroom.dto.business.BoardMapper;
import com.charmroom.charmroom.entity.Board;
import com.charmroom.charmroom.entity.enums.BoardType;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.repository.ArticleRepository;
import com.charmroom.charmroom.repository.BoardRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {
	private final BoardRepository boardRepository;
	private final ArticleRepository articleRepository;
	
	public BoardDto create(String name, BoardType type) {
		if (boardRepository.existsByName(name))
			throw new BusinessLogicException(BusinessLogicError.DUPLICATED_BOARD_NAME);
		Board board = Board.builder()
				.name(name)
				.type(type)
				.build();
		Board saved = boardRepository.save(board);
		return BoardMapper.toDto(saved);
	}
	
	public Page<BoardDto> getBoards(Pageable pageable){
		return boardRepository.findAll(pageable).map(board -> BoardMapper.toDto(board));
	}
	
	public Page<ArticleDto> getArticlesByBoardId(Integer boardId, Pageable pageable){
		Board board = loadById(boardId);
		return articleRepository.findAllByBoard(board, pageable).map(article -> ArticleMapper.toDto(article));
	}
	
	private Board loadById(Integer id) {
		return boardRepository.findById(id)
				.orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_BOARD, "id: " + id));
	}
	@Transactional
	public BoardDto changeType(Integer id, BoardType type) {
		Board board = loadById(id);
		
		board.updateType(type);
		return BoardMapper.toDto(board);
	}
	
	@Transactional
	public BoardDto changeExpose(Integer id, Boolean expose) {
		Board board = loadById(id);
		
		board.updateExposed(expose);
		return BoardMapper.toDto(board);
	}
	
	public void delete(Integer id) {
		Board board = loadById(id);
		
		boardRepository.delete(board);
	}
}
