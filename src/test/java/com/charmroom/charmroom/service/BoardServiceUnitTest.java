package com.charmroom.charmroom.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.charmroom.charmroom.entity.Article;
import com.charmroom.charmroom.entity.Board;
import com.charmroom.charmroom.entity.enums.BoardType;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.repository.ArticleRepository;
import com.charmroom.charmroom.repository.BoardRepository;

@ExtendWith(MockitoExtension.class)
public class BoardServiceUnitTest {
	@Mock
	private BoardRepository boardRepository;
	@Mock
	private ArticleRepository articleRepository;
	@InjectMocks
	private BoardService boardService;
	
	Board board;
	
	Board buildBoard(Integer id) {
		return Board.builder()
				.id(id)
				.name(id + "")
				.type(BoardType.LIST)
				.build();
	}
	
	Article buildArticle() {
		return Article.builder().build();
	}
	@BeforeEach
	void setup() {
		board = buildBoard(1);
	}
	
	@Nested
	class Create {
		@Test
		void success() {
			// given
			doReturn(false).when(boardRepository).existsByName(board.getName());
			doReturn(board).when(boardRepository).save(any(Board.class));
			
			// when
			var saved = boardService.create(board.getName(), board.getType());
			
			// then
			assertThat(saved).isNotNull();
		}
		
		@Test
		void fail() {
			doReturn(true).when(boardRepository).existsByName(board.getName());
			
			// when
			var thrown = assertThrows(BusinessLogicException.class, () -> {
				boardService.create(board.getName(), board.getType());
			});
			
			// then
			assertThat(thrown.getError()).isEqualTo(BusinessLogicError.DUPLICATED_BOARD_NAME);
		}
	}
	
	@Nested
	class getBoards{
		@Test
		void success() {
			// given
			var listBoard = List.of(buildBoard(1), buildBoard(2), buildBoard(3));
			var pageRequest = PageRequest.of(0, 3);
			var pageBoard = new PageImpl<>(listBoard);
			
			doReturn(pageBoard).when(boardRepository).findAll(pageRequest);
			
			// when
			var result = boardService.getBoards(pageRequest);
			
			// then
			assertThat(result).hasSize(3);
		}
	}
	
	@Nested
	class getArticlesByBoardId{
		@Test
		void success() {
			// given
			var listArticle = List.of(buildArticle(), buildArticle(), buildArticle());
			var pageRequest = PageRequest.of(0, 3);
			var pageArticle = new PageImpl<>(listArticle);
			
			doReturn(Optional.of(board)).when(boardRepository).findById(board.getId());
			doReturn(pageArticle).when(articleRepository).findAllByBoard(board, pageRequest);
			
			
			// when
			var result = boardService.getArticlesByBoardId(board.getId(), pageRequest);
			
			// then
			assertThat(result).hasSize(3);
		}
	}
	
	@Nested
	class ChangeType{
		@Test
		void success() {
			// given
			doReturn(Optional.of(board)).when(boardRepository).findById(board.getId());
			
			// when
			var result = boardService.changeType(board.getId(), BoardType.GALLERY);
			
			// then
			assertThat(result.getType()).isEqualTo(BoardType.GALLERY);
		}
	}
	
	@Nested
	class ChangeExpose {
		@Test
		void success() {
			// given
			doReturn(Optional.of(board)).when(boardRepository).findById(board.getId());
			
			// when
			var result = boardService.changeExpose(board.getId(), true);
			
			// then
			assertThat(result.getExposed()).isTrue();
		}
	}
	
	@Nested
	class Delete{
		@Test
		void success() {
			// given
			doReturn(Optional.of(board)).when(boardRepository).findById(board.getId());
			
			// when
			boardService.delete(board.getId());
			
			// then
			verify(boardRepository).delete(board);
		}
	}
	
	
	
}
