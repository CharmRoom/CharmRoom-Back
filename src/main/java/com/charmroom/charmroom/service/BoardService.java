package com.charmroom.charmroom.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.charmroom.charmroom.entity.Article;
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
	
	public Board create(String name, BoardType type) {
		if (boardRepository.existsByName(name))
			throw new BusinessLogicException(BusinessLogicError.DUPLICATED_BOARD_NAME);
		Board board = Board.builder()
				.name(name)
				.type(type)
				.build();
		return boardRepository.save(board);
	}
	
	public Page<Board> getBoards(Pageable pageable){
		return boardRepository.findAll(pageable);
	}
	
	public Page<Article> getArticlesByBoardId(Integer boardId, Pageable pageable){
		Board board = loadById(boardId);
		return articleRepository.findAllByBoard(board, pageable);
	}
	
	public Board loadById(Integer id) {
		return boardRepository.findById(id)
				.orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_BOARD, "id: " + id));
	}
	@Transactional
	public Board changeType(Integer id, BoardType type) {
		Board board = loadById(id);
		
		board.updateType(type);
		return board;
	}
	
	@Transactional
	public Board changeExpose(Integer id, Boolean expose) {
		Board board = loadById(id);
		
		board.updateExposed(expose);
		return board;
	}
	
	public void delete(Integer id) {
		Board board = loadById(id);
		
		boardRepository.delete(board);
	}
}
