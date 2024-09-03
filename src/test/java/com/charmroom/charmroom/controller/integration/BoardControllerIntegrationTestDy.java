package com.charmroom.charmroom.controller.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.charmroom.charmroom.controller.integration.IntegrationTestBase.WithCharmroomUserDetails;
import com.charmroom.charmroom.entity.Article;
import com.charmroom.charmroom.entity.Board;
import com.charmroom.charmroom.entity.enums.BoardType;
import com.charmroom.charmroom.repository.ArticleRepository;
import com.charmroom.charmroom.repository.BoardRepository;

@WithCharmroomUserDetails
public class BoardControllerIntegrationTestDy extends IntegrationTestBase {
	@Autowired
	BoardRepository boardRepository;
	String urlPrefix = "/api/board";
	
	Board buildBoard(String name, BoardType type, Boolean exposed) {
		return Board.builder()
				.name(name)
				.type(type)
				.exposed(exposed)
				.build();
	}
	
	@Nested
	class GetBoardsExposed{
		
		@Test
		void success() throws Exception {
			// given
			var boardList = List.of(
					buildBoard("1", BoardType.LIST, true)
					,buildBoard("2", BoardType.GALLERY, false)
					,buildBoard("3", BoardType.MARKET, true)
					);
					
			for(var b : boardList) {
				boardRepository.save(b);
			}
			mockMvc.perform(get(urlPrefix))
			.andExpectAll(
					status().isOk()
					,jsonPath("$.data").isArray()
					,jsonPath("$.data.size()").value(2)
					)
			.andDo(print())
			;
		}
	}
	
	@Nested
	class GetAllBoards{
		String url = urlPrefix + "/all";
		@Test
		void success() throws Exception {
			// given
			for(var i = 0; i < 20; i++) {
				Board b = buildBoard(Integer.toString(i), BoardType.values()[i % 3], i % 2 == 0);
				boardRepository.save(b);
			}
			// when
			mockMvc.perform(get(url))
			// then
			.andExpectAll(
					status().isOk()
					,jsonPath("$.data.totalElements").value(20)
					,jsonPath("$.data.content").isArray()
					,jsonPath("$.data.content.size()").value(10)
					)
			;
		}
	}
	
	@Nested
	class getBoard{
		@Test
		void success() throws Exception {
			// given
			Board b = boardRepository.save(buildBoard("123", BoardType.LIST, true));
			
			// when
			mockMvc.perform(get(urlPrefix + "/info/" + b.getId()))
			// then
			.andExpectAll(
					status().isOk()
					,jsonPath("$.data").exists()
					,jsonPath("$.data.name").value("123")
					);
		}
		
	}
	@Nested
	class GetArticles {
		@Autowired
		ArticleRepository articleRepository;
		
		String url = urlPrefix + "/";
		
		Article buildArticle(Board parent) {
			return Article.builder()
					.title("test")
					.body("test")
					.board(parent)
					.build();
		}
		@Test
		void success() throws Exception {
			// given
			Board b = buildBoard("name", BoardType.LIST, true);
			boardRepository.save(b);
			for(var i = 0; i < 15; i++) {
				articleRepository.save(buildArticle(b));
			}
			
			// when
			mockMvc.perform(get(url + b.getId()))
			.andExpectAll(
					status().isOk()
					,jsonPath("$.data.totalElements").value(15)
					,jsonPath("$.data.content").isArray()
					,jsonPath("$.data.content.size()").value(10)
					)
			;	
		}
		@Test
		void failByNotfoundBoard() throws Exception {
			// when
			mockMvc.perform(get(url + "12345"))
			.andExpectAll(
					status().isNotFound()
					,jsonPath("$.code").value("04100")
					);
		}
	}
}
