package com.charmroom.charmroom.controller.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import com.charmroom.charmroom.controller.api.CommentController;
import com.charmroom.charmroom.dto.business.CommentDto;
import com.charmroom.charmroom.dto.business.CommentLikeDto;
import com.charmroom.charmroom.dto.presentation.CommentDto.CommentCreateRequestDto;
import com.charmroom.charmroom.dto.presentation.CommentDto.CommentUpdateRequestDto;
import com.charmroom.charmroom.exception.ExceptionHandlerAdvice;
import com.charmroom.charmroom.service.CommentLikeService;
import com.charmroom.charmroom.service.CommentService;
import com.google.gson.Gson;

@ExtendWith(MockitoExtension.class)
public class CommentControllerUnitTestDy {
	@Mock CommentService commentService;
	@Mock CommentLikeService commentLikeService;
	
	@InjectMocks CommentController commentController;
	
	MockMvc mockMvc;
	CommentDto mockedCommentDto;
	
	Gson gson;
	
	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders
				.standaloneSetup(commentController)
				.setControllerAdvice(new ExceptionHandlerAdvice())
				.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
				.setValidator(mock(Validator.class))
				.build();
		mockedCommentDto = CommentDto.builder()
				.id(1)
				.body("test")
				.build();
		gson = new Gson();
	}
	
	@Nested
	class Create{
		@Test
		void successWhenNoParent() throws Exception {
			// given
			doReturn(mockedCommentDto).when(commentService)
			.create(eq(1), any(), eq(mockedCommentDto.getBody()));
			var request = CommentCreateRequestDto.builder()
					.body("test")
					.build();
			
			// when
			mockMvc.perform(
					post("/api/comment/1")
					.content(gson.toJson(request))
					.contentType(MediaType.APPLICATION_JSON)
					)
			// then
			.andExpectAll(
					status().isCreated(),
					jsonPath("$.data.body").value("test")
					)
			;
		}
		@Test
		void successWhenParentExists() throws Exception{
			// given
			mockedCommentDto.setParent(mockedCommentDto);
			doReturn(mockedCommentDto).when(commentService).create(eq(1), any(), eq("test"), eq(1));
			var request = CommentCreateRequestDto.builder()
					.body("test")
					.parentId(1)
					.build();
			// when
			mockMvc.perform(
					post("/api/comment/1")
					.content(gson.toJson(request))
					.contentType(MediaType.APPLICATION_JSON)
					)
			// then
			.andExpectAll(
					status().isCreated(),
					jsonPath("$.data.body").value("test"),
					jsonPath("$.data.parentId").value(1)
					)
			;
		}
	}
	@Nested
	class GetCommentList{
		@Test
		void success() throws Exception {
			// given
			CommentDto mockedChild = CommentDto.builder()
					.body("child")
					.build();
			List<CommentDto> childList = List.of(mockedChild);
			mockedCommentDto.setChildList(childList);
			var dtoList = List.of(mockedCommentDto, mockedCommentDto, mockedCommentDto);
			var pageRequest = PageRequest.of(0, 10, Sort.by("id").descending());
			var dtoPage = new PageImpl<>(dtoList, pageRequest, 3);
			doReturn(dtoPage).when(commentService).getComments(1, pageRequest);
			
			// when
			mockMvc.perform(get("/api/comment/1"))
			// then
			.andExpectAll(
					status().isOk(),
					jsonPath("$.data.content").isArray(),
					jsonPath("$.data.content.size()").value(3),
					jsonPath("$.data.content[0].body").value("test"),
					jsonPath("$.data.content[0].childList").isArray(),
					jsonPath("$.data.content[0].childList.size()").value(1),
					jsonPath("$.data.content[0].childList[0].body").value("child")
					)
			;
		}
	}
	
	@Nested
	class Update{
		@Test
		void success() throws Exception {
			// given
			doReturn(mockedCommentDto).when(commentService)
			.update(eq(1), any(), eq(mockedCommentDto.getBody()));
			CommentUpdateRequestDto request = CommentUpdateRequestDto.builder()
					.body(mockedCommentDto.getBody())
					.build();
			// when
			mockMvc.perform(
					patch("/api/comment/1")
					.content(gson.toJson(request))
					.contentType(MediaType.APPLICATION_JSON))
			
			// then
			.andExpectAll(
					status().isOk()
					,jsonPath("$.data.body").value(mockedCommentDto.getBody())
					)
			;
			
		}
	}
	
	@Nested
	class Delete{
		@Test
		void success() throws Exception{
			// given
			mockedCommentDto.setDisabled(true);
			doReturn(mockedCommentDto).when(commentService).disable(eq(1), any());
			
			// when
			mockMvc.perform(delete("/api/comment/1"))
			// then
			.andExpectAll( 
					status().isOk()
					,jsonPath("$.data.disabled").value(true)
					);
			verify(commentService).disable(eq(1), any());
		}
	}
	
	@Nested
	class Like{
		@Test
		void success() throws Exception{
			// given
			var mockedCommentLikeDto = CommentLikeDto.builder()
					.type(true)
					.build();
			doReturn(mockedCommentLikeDto).when(commentLikeService).like(any(), eq(1));
			// when
			mockMvc.perform(post("/api/comment/like/1"))
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
		@Test
		void success() throws Exception{
			// given
			var mockedCommentLikeDto = CommentLikeDto.builder()
					.type(false)
					.build();
			doReturn(mockedCommentLikeDto).when(commentLikeService).dislike(any(), eq(1));
			// when
			mockMvc.perform(post("/api/comment/dislike/1"))
			// then
			.andExpectAll(
					status().isOk()
					,jsonPath("$.data.type").value(false)
					)
			;
		}
	}
}
