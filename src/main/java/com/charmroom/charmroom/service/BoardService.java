package com.charmroom.charmroom.service;

import java.util.List;

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
	
	public BoardDto create(String name, String type) {
		if (boardRepository.existsByName(name))
			throw new BusinessLogicException(BusinessLogicError.DUPLICATED_BOARD_NAME);
		Board board = Board.builder()
				.name(name)
				.type(BoardType.valueOf(type))
				.build();
		Board saved = boardRepository.save(board);
		return BoardMapper.toDto(saved);
	}
	
	public Page<BoardDto> getBoards(Pageable pageable){
		return boardRepository.findAll(pageable).map(BoardMapper::toDto);
	}
	
	public List<BoardDto> getBoardsExposed(){
		List<Board> boards = boardRepository.findAllByExposed(true);
		return boards.stream().map(BoardMapper::toDto).toList();
	}
	
	public Page<ArticleDto> getArticlesByBoardId(Integer boardId, Pageable pageable){
		Board board = loadById(boardId);
		return articleRepository.findAllByBoard(board, pageable).map(ArticleMapper::toDto);
	}
	
	private Board loadById(Integer id) {
		return boardRepository.findById(id)
				.orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_BOARD, "id: " + id));
	}
	
	public BoardDto changeName(Integer id, String name) {
		Board board = loadById(id);
		board.updateName(name);
		return BoardMapper.toDto(board);
	}
	
	@Transactional
	public BoardDto changeType(Integer id, String type) {
		Board board = loadById(id);
		
		board.updateType(BoardType.valueOf(type));
		return BoardMapper.toDto(board);
	}
	
	@Transactional
	public BoardDto changeExpose(Integer id, boolean exposed) {
		Board board = loadById(id);
		
		board.updateExposed(exposed);
		return BoardMapper.toDto(board);
	}
	
	@Transactional 
	public BoardDto update(Integer id, String name, String type) {
		Board board = loadById(id);
		board.updateName(name);
		board.updateType(BoardType.valueOf(type));
		return BoardMapper.toDto(board);
	}
	
	public void delete(Integer id) {
		Board board = loadById(id);
		
		boardRepository.delete(board);
	}
}
