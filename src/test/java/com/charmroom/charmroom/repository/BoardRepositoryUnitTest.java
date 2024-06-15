package com.charmroom.charmroom.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import com.charmroom.charmroom.entity.Board;
import com.charmroom.charmroom.entity.enums.BoardType;

@DataJpaTest
@TestPropertySource(properties = {"spring.config.location = classpath:application-test.yml"})
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Board Repository 단위 테스트")
public class BoardRepositoryUnitTest {
	@Autowired
	private BoardRepository boardRepository;
	
	private Board board = buildBoard();
	
	private Board buildBoard() {
		return Board.builder()
				.type(BoardType.LIST)
				.build();
	}
	
	@Nested
	@DisplayName("CREATE")
	class CreateBoard {
		@Test
		public void success() {
			// given
			// when
			Board savedBoard = boardRepository.save(board);
			// then
			assertThat(savedBoard).isNotNull();
			assertThat(savedBoard).isEqualTo(board);
		}
	}
	
	@Nested
	@DisplayName("READ")
	class ReadBoard {
		@Test
		public void success() {
			// given
			Board savedBoard = boardRepository.save(board);
			
			// when
			var foundBoard = boardRepository.findById(board.getId());
			// then
			assertThat(foundBoard).isPresent();
			assertThat(foundBoard).get().isEqualTo(savedBoard);
		}
		
		@Test
		public void fail() {
			// given
			boardRepository.save(board);
			
			// when
			var foundBoard = boardRepository.findById(12345);
			
			// then
			assertThat(foundBoard).isNotPresent();
		}
	}
	
	@Nested
	@DisplayName("UPDATE")
	class UpdateBoard {
		@Test
		public void success() {
			// given
			Board savedBoard = boardRepository.save(board);
			
			// when
			savedBoard.updateType(BoardType.GALLERY);
			savedBoard.updateExposed(true);
			// then
			var foundBoard = boardRepository.findById(board.getId()).get();
			assertThat(foundBoard).isNotNull();
			assertThat(foundBoard.getType()).isEqualTo(BoardType.GALLERY);
			assertThat(foundBoard.getExposed()).isEqualTo(true);
		}
	}
	
	@Nested
	@DisplayName("DELETE")
	class DeleteBoard {
		@Test
		public void success() {
			// given
			Board savedBoard = boardRepository.save(board);
			
			// when
			boardRepository.delete(savedBoard);
			
			// then
			var foundBoards = boardRepository.findAll();
			assertThat(foundBoards).isNotNull();
			assertThat(foundBoards).isEmpty();
		}
	}
	
	
}
