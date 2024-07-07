package com.charmroom.charmroom.controller.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.charmroom.charmroom.controller.integration.IntegrationTestBase.WithCharmroomUserDetails;
import com.charmroom.charmroom.dto.presentation.CommentDto.CommentCreateRequestDto;
import com.charmroom.charmroom.dto.presentation.CommentDto.CommentUpdateRequestDto;
import com.charmroom.charmroom.entity.Article;
import com.charmroom.charmroom.entity.Board;
import com.charmroom.charmroom.entity.Comment;
import com.charmroom.charmroom.entity.CommentLike;
import com.charmroom.charmroom.entity.enums.BoardType;
import com.charmroom.charmroom.repository.ArticleRepository;
import com.charmroom.charmroom.repository.BoardRepository;
import com.charmroom.charmroom.repository.CommentLikeRepository;
import com.charmroom.charmroom.repository.CommentRepository;

@WithCharmroomUserDetails
public class CommentControllerIntegrationTestDy extends IntegrationTestBase {
	@Autowired
	BoardRepository boardRepository;
	@Autowired
	ArticleRepository articleRepository;
	@Autowired
	CommentRepository commentRepository;
	
	String urlPrefix = "/api/comment/";
	
	Board board = Board.builder()
			.name("board")
			.type(BoardType.LIST)
			.build();
	Article article = Article.builder()
			.board(board)
			.title("test")
			.body("test")
			.build();
	
	Comment buildComment(String body, Comment parent) {
		return Comment.builder()
				.article(article)
				.user(charmroomUser)
				.body(body)
				.parent(parent)
				.build();
	}
	Comment buildComment(String body) {
		return Comment.builder()
				.article(article)
				.user(charmroomUser)
				.body(body)
				.build();
	}
	
	@Nested
	class Create{
		String url = urlPrefix;
		CommentCreateRequestDto dto = CommentCreateRequestDto.builder()
				.body("123")
				.build();
		
		@Test
		void success() throws Exception {
			// given
			boardRepository.save(board);
			Article savedArticle = articleRepository.save(article);
			
			// when
			mockMvc.perform(post(url + savedArticle.getId())
					.content(gson.toJson(dto))
					.contentType(MediaType.APPLICATION_JSON)
					)
			// then
			.andExpectAll(
					status().isCreated()
					,jsonPath("$.data.body").value(dto.getBody())
					)
			;
		}
		@Test
		void successWithParentComment() throws Exception{
			// given
			boardRepository.save(board);
			Article savedArticle = articleRepository.save(article);
			Comment parent = commentRepository.save(Comment.builder()
					.article(savedArticle)
					.user(charmroomUser)
					.body("parent")
					.build());
			
			var childDto = CommentCreateRequestDto.builder()
					.body("child")
					.parentId(parent.getId())
					.build();
			// when
			mockMvc.perform(post(url+savedArticle.getId())
					.content(gson.toJson(childDto))
					.contentType(MediaType.APPLICATION_JSON)
					)
			.andExpectAll(
					status().isCreated()
					,jsonPath("$.data.body").value(childDto.getBody())
					,jsonPath("$.data.parent.body").value(parent.getBody())
					)
			;
			
		}
		@Test
		void failByNotFoundArticle() throws Exception {
			// given
			
			// when
			mockMvc.perform(post(url + "12345")
					.content(gson.toJson(dto))
					.contentType(MediaType.APPLICATION_JSON)
					)
			// then
			.andExpectAll(
					status().isNotFound()
					,jsonPath("$.code").value("01100")
					)
			;
			
		}
	}
	
	@Nested
	class GetCommentList {
		String url = urlPrefix;
		@Test
		void success() throws Exception {
			// given
			boardRepository.save(board);
			Article savedArticle = articleRepository.save(article);
			
			for(var i = 0; i < 3; i++) {
				Comment p = commentRepository.save(buildComment(Integer.toString(i)));
				for(var j = 0; j < 3; j++) {
					Comment c = commentRepository.save(
							buildComment(Integer.toString(i) + "_" + Integer.toString(j), p));
					p.getChildList().add(c);
				}
			}

			// when
			mockMvc.perform(get(url + savedArticle.getId()))
			// then
			.andExpectAll(
					status().isOk()
					,jsonPath("$.data.totalElements").value(12)
					,jsonPath("$.data.content").isArray()
					,jsonPath("$.data.content.size()").value(10)
					,jsonPath("$.data.content[0].body").value("2_2")
					,jsonPath("$.data.content[0].parent.body").value("2")
					,jsonPath("$.data.content[3].childList").isArray()
					,jsonPath("$.data.content[3].childList.size()").value(3)
					)
			;
		}
	}
	
	@Nested
	class Update {
		String url = urlPrefix;
		CommentUpdateRequestDto dto = CommentUpdateRequestDto.builder()
				.body("changed")
				.build();
		@Test
		void success() throws Exception {
			// given
			boardRepository.save(board);
			articleRepository.save(article);
			Comment saved = commentRepository.save(buildComment("test"));
			
			// when
			mockMvc.perform(patch(url + saved.getId())
					.content(gson.toJson(dto))
					.contentType(MediaType.APPLICATION_JSON)
					)
			// then
			.andExpectAll(
					status().isOk()
					,jsonPath("$.data.body").value("changed")
					)
			;
		}
		
		@Test
		void failByNotFoundComment() throws Exception {
			// given
			
			// when
			mockMvc.perform(patch(url + "12345")
					.content(gson.toJson(dto))
					.contentType(MediaType.APPLICATION_JSON)
					)
			// then
			.andExpectAll(
					status().isNotFound()
					,jsonPath("$.code").value("06100")
					)
			;
		}
	}
	
	@Nested
	class Disable{
		String url = urlPrefix;
		@Test
		void success() throws Exception {
			// given 
			boardRepository.save(board);
			articleRepository.save(article);
			Comment saved = commentRepository.save(buildComment("test"));
			
			// when
			mockMvc.perform(delete(url + saved.getId()))
			// then
			.andExpectAll(
					status().isOk()
					,jsonPath("$.data.disabled").value(true)
					)
			;
			
		}
	}
	
	@Nested
	class Like{
		@Autowired
		CommentLikeRepository commentLikeRepository;
		
		String url = urlPrefix + "like/";
		@Test
		void success() throws Exception {
			// given
			boardRepository.save(board);
			articleRepository.save(article);
			Comment saved = commentRepository.save(buildComment("test"));
			
			// when
			mockMvc.perform(post(url + saved.getId()))
			// then
			.andExpectAll(
					status().isOk()
					,jsonPath("$.data.type").value(true)
					)
			;
		}
		@Test
		void successWhenAlreadyLike() throws Exception {
			// given
			boardRepository.save(board);
			articleRepository.save(article);
			Comment saved = commentRepository.save(buildComment("test"));
			commentLikeRepository.save(CommentLike.builder()
					.user(charmroomUser)
					.comment(saved)
					.type(true)
					.build());
			
			// when
			mockMvc.perform(post(url + saved.getId()))
			// then
			.andExpectAll(
					status().isOk()
					,jsonPath("$.data").doesNotExist()
				)
			;
		}
		
		@Test
		void successWhenAlreadyDislike() throws Exception {
			// given
			boardRepository.save(board);
			articleRepository.save(article);
			Comment saved = commentRepository.save(buildComment("test"));
			commentLikeRepository.save(CommentLike.builder()
					.user(charmroomUser)
					.comment(saved)
					.type(false)
					.build());
			
			// when
			mockMvc.perform(post(url + saved.getId()))
			// then
			.andExpectAll(
					status().isOk()
					,jsonPath("$.data.type").value(true)
				)
			;
		}
	}
	
	@Nested
	class Dislike{
		@Autowired
		CommentLikeRepository commentLikeRepository;
		
		String url = urlPrefix + "dislike/";
		@Test
		void success() throws Exception {
			// given
			boardRepository.save(board);
			articleRepository.save(article);
			Comment saved = commentRepository.save(buildComment("test"));
			
			// when
			mockMvc.perform(post(url + saved.getId()))
			// then
			.andExpectAll(
					status().isOk()
					,jsonPath("$.data.type").value(false)
					)
			;
		}
		@Test
		void successWhenAlreadyLike() throws Exception {
			// given
			boardRepository.save(board);
			articleRepository.save(article);
			Comment saved = commentRepository.save(buildComment("test"));
			commentLikeRepository.save(CommentLike.builder()
					.user(charmroomUser)
					.comment(saved)
					.type(true)
					.build());
			
			// when
			mockMvc.perform(post(url + saved.getId()))
			// then
			.andExpectAll(
					status().isOk()
					,jsonPath("$.data.type").value(false)
				)
			;
		}
		
		@Test
		void successWhenAlreadyDislike() throws Exception {
			// given
			boardRepository.save(board);
			articleRepository.save(article);
			Comment saved = commentRepository.save(buildComment("test"));
			commentLikeRepository.save(CommentLike.builder()
					.user(charmroomUser)
					.comment(saved)
					.type(false)
					.build());
			
			// when
			mockMvc.perform(post(url + saved.getId()))
			// then
			.andExpectAll(
					status().isOk()
					,jsonPath("$.data").doesNotExist()
				)
			;
		}
	}
}
