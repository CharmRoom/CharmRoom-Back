package com.charmroom.charmroom.controller.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
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

import com.charmroom.charmroom.controller.api.AdminController;
import com.charmroom.charmroom.dto.business.BoardDto;
import com.charmroom.charmroom.dto.business.UserDto;
import com.charmroom.charmroom.dto.business.UserMapper;
import com.charmroom.charmroom.dto.presentation.BoardDto.CreateBoardRequestDto;
import com.charmroom.charmroom.dto.presentation.BoardDto.UpdateBoardRequestDto;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.entity.enums.BoardType;
import com.charmroom.charmroom.entity.enums.UserLevel;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.exception.ExceptionHandlerAdvice;
import com.charmroom.charmroom.service.BoardService;
import com.charmroom.charmroom.service.UserService;
import com.google.gson.Gson;

@ExtendWith(MockitoExtension.class)
public class AdminControllerUnitTestDy {
	@Mock
	UserService userService;
	@Mock
	BoardService boardService;
	
	@InjectMocks
	AdminController adminController;
	
	MockMvc mockMvc;
	
	UserDto mockedUserDto;
	BoardDto mockedBoardDto;
	
	Gson gson;
	
	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders
				.standaloneSetup(adminController)
				.setControllerAdvice(new ExceptionHandlerAdvice())
				.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
				.setValidator(mock(Validator.class))
				.build();
		
		mockedUserDto = UserMapper.toDto(User.builder()
				.username("test")
				.password("password")
				.email("test@test.com")
				.nickname("nickname")
				.build());
		mockedBoardDto = BoardDto.builder()
				.id(1)
				.name("board")
				.type(BoardType.LIST)
				.exposed(false)
				.build();
		gson = new Gson();
	}
	
	@Nested
	class Users{
		@Test
		void success() throws Exception {
			// given
			var dtoList = List.of(mockedUserDto, mockedUserDto, mockedUserDto);
			var pageRequest = PageRequest.of(0, 10, Sort.by("id").descending());
			var dtoPage = new PageImpl<>(dtoList, pageRequest, 3);
			
			doReturn(dtoPage).when(userService).getAllUsersByPageable(pageRequest);
			
			// when
			mockMvc.perform(get("/api/admin/user"))
			
			// then
			.andExpectAll(status().isOk(),
					jsonPath("$.data.totalElements").value(3),
					jsonPath("$.data.content").isArray(),
					jsonPath("$.data.content[0].username").value(mockedUserDto.getUsername()),
					jsonPath("$.data.content[0].password").doesNotExist()
					)
			;
			
		}
	}
	
	@Nested
	class ChangeUserGrade{
		@Test
		void success() throws Exception{
			// given
			mockedUserDto.setLevel(UserLevel.ROLE_ADMIN);
			doReturn(mockedUserDto).when(userService).changeLevel(mockedUserDto.getUsername(), UserLevel.ROLE_ADMIN.getValue());
			
			// when
			mockMvc.perform(patch("/api/admin/user/grade")
					.param("username", mockedUserDto.getUsername())
					.param("grade", UserLevel.ROLE_ADMIN.getValue())
					)
			.andExpectAll(status().isOk(),
					jsonPath("$.data.level").value(UserLevel.ROLE_ADMIN.getValue()));
			;
		}
		@Test
		void fail() throws Exception{
			// given
			doThrow(new BusinessLogicException(BusinessLogicError.NOTFOUND_USER))
			.when(userService).changeLevel(any(), any());
			
			// when
			mockMvc.perform(patch("/api/admin/user/grade")
					.param("username", mockedUserDto.getUsername())
					.param("grade", UserLevel.ROLE_ADMIN.getValue())
					)
			.andExpect(status().isNotFound())
			;
		}
	}
	
	@Nested
	class ChangeUserWithdraw{
		@Test
		void success() throws Exception{
			// given
			mockedUserDto.setWithdraw(true);
			doReturn(mockedUserDto).when(userService).changeWithdraw(mockedUserDto.getUsername(), true);
			
			// when
			mockMvc.perform(patch("/api/admin/user/withdraw")
					.param("username", mockedUserDto.getUsername()))
			.andExpect(status().isOk());
		}
	}
	
	@Nested
	class CreateBoard{
		@Test
		void success() throws Exception{
			// given
			doReturn(mockedBoardDto).when(boardService)
			.create(mockedBoardDto.getName(), mockedBoardDto.getType().toString());
			
			CreateBoardRequestDto dto = CreateBoardRequestDto.builder()
					.name(mockedBoardDto.getName())
					.type(mockedBoardDto.getType().toString())
					.build();
			// when
			mockMvc.perform(
					post("/api/admin/board")
					.content(gson.toJson(dto))
					.contentType(MediaType.APPLICATION_JSON)
					)
			// then
			.andExpectAll(
					status().isCreated(),
					jsonPath("$.data.name").value(mockedBoardDto.getName()) ,
					jsonPath("$.data.type").value(mockedBoardDto.getType().toString()),
					jsonPath("$.data.exposed").value(mockedBoardDto.isExposed())
					)
			;
		}
	}
	
	@Nested
	class UpdateBoard{
		@Test
		void success() throws Exception{
			// given
			doReturn(mockedBoardDto).when(boardService)
			.update(mockedBoardDto.getId(),
					mockedBoardDto.getName(),
					mockedBoardDto.getType().toString());
			
			UpdateBoardRequestDto dto = UpdateBoardRequestDto.builder()
					.name(mockedBoardDto.getName())
					.type(mockedBoardDto.getType().toString())
					.build();
			
			// when
			mockMvc.perform(post("/api/admin/board/" + mockedBoardDto.getId())
					.content(gson.toJson(dto))
					.contentType(MediaType.APPLICATION_JSON)
					)
			// then
			.andExpectAll(
					status().isOk(),
					jsonPath("$.data.name").value(mockedBoardDto.getName()) ,
					jsonPath("$.data.type").value(mockedBoardDto.getType().toString()),
					jsonPath("$.data.exposed").value(mockedBoardDto.isExposed())
					)
			;	
		}
	}
	@Nested
	class ExposeBoard{
		@Test
		void success() throws Exception{
			// given
			mockedBoardDto.setExposed(true);
			doReturn(mockedBoardDto).when(boardService).changeExpose(mockedBoardDto.getId(), true);
			
			// when
			mockMvc.perform(patch("/api/admin/board/"+mockedBoardDto.getId())
					.param("exposed", "true")
					)
			// then
			.andExpectAll(
					status().isOk(),
					jsonPath("$.data.exposed").value(true)
					)
			;
		}
	}
	@Nested
	class DeleteBoard{
		@Test
		void success() throws Exception{
			// given
			doNothing().when(boardService).delete(mockedBoardDto.getId());
			
			// when
			mockMvc.perform(delete("/api/admin/board/"+mockedBoardDto.getId()))

			// then
			.andExpect(status().isOk());
		}
	}
}
