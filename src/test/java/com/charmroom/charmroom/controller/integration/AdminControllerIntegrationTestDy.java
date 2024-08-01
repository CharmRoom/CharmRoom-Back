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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.charmroom.charmroom.controller.integration.IntegrationTestBase.WithCharmroomAdminDetails;
import com.charmroom.charmroom.dto.presentation.BoardDto.BoardCreateRequestDto;
import com.charmroom.charmroom.dto.presentation.BoardDto.BoardUpdateRequestDto;
import com.charmroom.charmroom.dto.presentation.PointDto.PointCreateRequestDto;
import com.charmroom.charmroom.entity.Board;
import com.charmroom.charmroom.entity.Image;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.entity.enums.BoardType;
import com.charmroom.charmroom.entity.enums.PointType;
import com.charmroom.charmroom.entity.enums.UserLevel;
import com.charmroom.charmroom.repository.BoardRepository;
import com.charmroom.charmroom.repository.ImageRepository;
import com.charmroom.charmroom.repository.UserRepository;

@WithCharmroomAdminDetails
public class AdminControllerIntegrationTestDy extends IntegrationTestBase {
	String urlPrefix = "/api/admin";
	@Nested
	class Users{
		MockHttpServletRequestBuilder request = get(urlPrefix + "/user");
		
		@Autowired
		UserRepository userRepository;
		@Autowired
		ImageRepository imageRepository;
		
		User buildUser(String prefix) {
			Image image = Image.builder()
					.originalName(prefix)
					.path(prefix)
					.build();
			imageRepository.save(image);
			return User.builder()
					.username(prefix + "username")
					.email(prefix + "email@email.com")
					.password(prefix + "password")
					.nickname(prefix + "nickname")
					.image(image)
					.level(UserLevel.ROLE_BASIC)
					.build();
		}
		
		@Test
		void success() throws Exception {
			// given
			for (var i = 0; i < 10; i++) {
				userRepository.save(buildUser(Integer.toString(i)));
			}
			// when
			mockMvc.perform(request)
			// then
			.andExpectAll(
					status().isOk()
					,jsonPath("$.data.totalElements").value(12) // already 2 user created by superclass
					,jsonPath("$.data.content").isArray()
					,jsonPath("$.data.content.size()").value(10)
					)
			;
		}
		@Test
		@WithCharmroomUserDetails
		void failByPermission() throws Exception {
			// given
			// when
			mockMvc.perform(request)
			// then
			.andExpect(status().isForbidden());
		}
	}
	
	@Nested
	class ChangeUserGrade{
		MockHttpServletRequestBuilder request = patch(urlPrefix + "/user/grade");
		@Test
		void success() throws Exception{
			// given
			// when
			mockMvc.perform(request
					.param("username", charmroomUser.getUsername())
					.param("grade", UserLevel.ROLE_ADMIN.toString())
					)
			.andExpectAll(
					status().isOk()
					,jsonPath("$.data.level").value(UserLevel.ROLE_ADMIN.toString())
					)
			;
		}
		
		@Test
		void failByNotfoundUser() throws Exception {
			// given
			// when
			mockMvc.perform(request
					.param("username", "SomeRandomName")
					.param("grade", UserLevel.ROLE_ADMIN.toString())
					)
			// then
			.andExpectAll(
					status().isNotFound()
					,jsonPath("$.code").value("12100")
					)
			;
		}
	}
	
	@Nested
	class ChangeUserWithdraw{
		MockHttpServletRequestBuilder request = patch(urlPrefix + "/user/withdraw");
		
		@Test
		void success() throws Exception {
			// given
			// when
			mockMvc.perform(request
					.param("username", charmroomUser.getUsername())
					.param("withdraw", "true")
					)
			// then
			.andExpectAll(
					status().isOk()
					,jsonPath("$.data.withdraw").value(true)
					)
			;
		}
	}
	
	@Nested
	class GivePoint {
		
		@Test
		void success() throws Exception {
			// given
			PointCreateRequestDto dto = PointCreateRequestDto.builder()
					.type(PointType.EARN.toString())
					.diff(300)
					.build(); 
			// when
			mockMvc.perform(post(urlPrefix + "/point/" + charmroomUser.getUsername())
					.content(gson.toJson(dto))
					.contentType(MediaType.APPLICATION_JSON)
					)
			// then
			.andExpectAll(
					status().isCreated()
					,jsonPath("$.data.type").value(PointType.EARN.toString())
					,jsonPath("$.data.diff").value(300)
					)
			;
		}
		
		@Test
		void invalidType() throws Exception {
			// given
			PointCreateRequestDto dto = PointCreateRequestDto.builder()
					.type("random")
					.diff(300)
					.build(); 
			// when
			mockMvc.perform(post(urlPrefix + "/point/" + charmroomUser.getUsername())
					.content(gson.toJson(dto))
					.contentType(MediaType.APPLICATION_JSON)
					)
			// then
			.andExpectAll(
					status().isNotAcceptable()
					,jsonPath("$.code").value("INVALID")
					,jsonPath("$.data.type").exists()
					)
			;
		}
	}
	
	@Nested
	class CreateBoard {
		MockHttpServletRequestBuilder request = post(urlPrefix + "/board");
		@Test
		void success() throws Exception {
			// given
			BoardCreateRequestDto dto = BoardCreateRequestDto.builder()
					.name("board")
					.type(BoardType.LIST.toString())
					.build();
			// when
			mockMvc.perform(request
					.content(gson.toJson(dto))
					.contentType(MediaType.APPLICATION_JSON)
					)
			// then
			.andExpectAll(
					status().isCreated()
					,jsonPath("$.data.name").value("board")
					,jsonPath("$.data.type").value(BoardType.LIST.toString())
					)
			;
		}
		@Test
		void invalid() throws Exception {
			// given
			BoardCreateRequestDto dto = BoardCreateRequestDto.builder()
					.name("")
					.type("random")
					.build();
			// when
			mockMvc.perform(request
					.content(gson.toJson(dto))
					.contentType(MediaType.APPLICATION_JSON)
					)
			// then
			.andExpectAll(
					status().isNotAcceptable()
					,jsonPath("$.code").value("INVALID")
					,jsonPath("$.data.name").exists()
					,jsonPath("$.data.type").exists()
					);
		}
	}
	
	@Nested
	class UpdateBoard {
		@Autowired
		BoardRepository boardRepository;
		Board board = Board.builder()
				.name("original")
				.type(BoardType.LIST)
				.build();
		BoardUpdateRequestDto dto = BoardUpdateRequestDto.builder()
				.name("changed")
				.type(BoardType.GALLERY.toString())
				.build();
		@Test
		void success() throws Exception {
			// given
			board = boardRepository.save(board);
			// when
			mockMvc.perform(post(urlPrefix + "/board/" + board.getId())
					.content(gson.toJson(dto))
					.contentType(MediaType.APPLICATION_JSON)
					)
			.andExpectAll(
					status().isOk()
					,jsonPath("$.data.name").value("changed")
					,jsonPath("$.data.type").value(BoardType.GALLERY.toString())
					)
			;
		}
		
		@Test
		void failByNotfoundBoardId() throws Exception {
			// given
			
			// when
			mockMvc.perform(post(urlPrefix + "/board/123456")
					.content(gson.toJson(dto))
					.contentType(MediaType.APPLICATION_JSON)
					)
			.andExpectAll(
					status().isNotFound()
					,jsonPath("$.code").value("04100")
					)
			;
			
		}
	}
	
	@Nested
	class ExposeBoard{
		@Autowired
		BoardRepository boardRepository;
		Board board = Board.builder()
				.name("original")
				.type(BoardType.LIST)
				.build();
		@Test
		void success() throws Exception {
			// given
			board = boardRepository.save(board);
			// when
			mockMvc.perform(patch(urlPrefix + "/board/" + board.getId())
					.param("exposed", "true")
					)
			// then
			.andExpectAll(
					status().isOk()
					,jsonPath("$.data.exposed").value(true)
					)
			;
		}
	}
	
	@Nested
	class DeleteBoard{
		@Autowired
		BoardRepository boardRepository;
		Board board = Board.builder()
				.name("original")
				.type(BoardType.LIST)
				.build();
		@Test
		void success() throws Exception {
			// given
			board = boardRepository.save(board);
			// when
			mockMvc.perform(delete(urlPrefix + "/board/" + board.getId()))
			// then
			.andExpect(status().isOk())
			;
		}
	}
	
}
