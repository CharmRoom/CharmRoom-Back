package com.charmroom.charmroom.controller.unit;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import com.charmroom.charmroom.controller.api.BoardController;
import com.charmroom.charmroom.dto.business.ArticleDto;
import com.charmroom.charmroom.dto.business.BoardDto;
import com.charmroom.charmroom.entity.enums.BoardType;
import com.charmroom.charmroom.exception.ExceptionHandlerAdvice;
import com.charmroom.charmroom.service.BoardService;
import com.google.gson.Gson;

@ExtendWith(MockitoExtension.class)
public class BoardControllerUnitTestDy {
	@Mock BoardService boardService;
	@InjectMocks BoardController boardController;

	MockMvc mockMvc;
	BoardDto mockedBoardDto;
	
	Gson gson;
	
	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders
				.standaloneSetup(boardController)
				.setControllerAdvice(new ExceptionHandlerAdvice())
				.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
				.setValidator(mock(Validator.class))
				.build();
		
		mockedBoardDto = BoardDto.builder()
				.id(1)
				.name("board")
				.type(BoardType.LIST)
				.exposed(false)
				.build();
		gson = new Gson();
	}
	
	@Nested
	class GetBoardsExposed{
		@Test
		void success() throws Exception{

			var dtoList = List.of(mockedBoardDto, mockedBoardDto, mockedBoardDto, mockedBoardDto);
			doReturn(dtoList).when(boardService).getBoardsExposed();
			
			// when
			mockMvc.perform(get("/api/board"))
			// then
			.andExpectAll(status().isOk(),
					jsonPath("$.data").isArray(),
					jsonPath("$.data.size()").value(4)
					)
			.andDo(print())
			;	
		}
	}
	@Nested
	class GetBoards{
		@Test
		void success() throws Exception{
			// given
			var dtoList = List.of(mockedBoardDto, mockedBoardDto);
			var pageRequest = PageRequest.of(0, 10, Sort.by("id").descending());
			var dtoPage = new PageImpl<>(dtoList, pageRequest, 2);
			
			doReturn(dtoPage).when(boardService).getBoards(pageRequest);
			
			// when
			mockMvc.perform(get("/api/board/all"))
			
			// then
			.andExpectAll(
					status().isOk(),
					jsonPath("$.data.totalElements").value(2),
					jsonPath("$.data.content").isArray(),
					jsonPath("$.data.content[1].name").value(mockedBoardDto.getName()),
					jsonPath("$.data.content[2]").doesNotExist()
					)
			;
		}
	}
	@Nested
	class GetBoard{
		@Test
		void success() throws Exception {
			// given 
			var dto = mockedBoardDto;
			doReturn(dto).when(boardService).getBoard(1);
			// when
			mockMvc.perform(get("/api/board/info/1"))
			// then
			.andExpectAll(
					status().isOk(),
					jsonPath("$.data").exists(),
					jsonPath("$.data.name").value(mockedBoardDto.getName())
					)
			;
		}
	}
	@Nested
	class GetArticles{
		@Test
		void success() throws Exception{
			// given
			ArticleDto dto = ArticleDto.builder()
					.id(1)
					.build();
			var dtoList = List.of(dto, dto, dto, dto, dto, dto);
			var pageRequest = PageRequest.of(0, 10, Sort.by("id").descending());
			var dtoPage = new PageImpl<>(dtoList, pageRequest, 6);
			
			doReturn(dtoPage).when(boardService).getArticlesByBoardId(mockedBoardDto.getId(), pageRequest);
			
			// when
			mockMvc.perform(get("/api/board/" + mockedBoardDto.getId()))
			.andExpectAll(
					status().isOk(),
					jsonPath("$.data.totalElements").value(6),
					jsonPath("$.data.content").isArray(),
					jsonPath("$.data.content.length()").value(6),
					jsonPath("$.data.content[0].id").value(1)
					);
			;
		}
	}
}
