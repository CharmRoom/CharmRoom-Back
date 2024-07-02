package com.charmroom.charmroom.controller.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
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

import com.charmroom.charmroom.controller.api.UserController;
import com.charmroom.charmroom.dto.business.PointMapper;
import com.charmroom.charmroom.dto.business.UserDto;
import com.charmroom.charmroom.dto.business.UserMapper;
import com.charmroom.charmroom.dto.presentation.UserDto.UserUpdateRequest;
import com.charmroom.charmroom.entity.Point;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.entity.enums.PointType;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.exception.ExceptionHandlerAdvice;
import com.charmroom.charmroom.service.PointService;
import com.charmroom.charmroom.service.UserService;
import com.google.gson.Gson;

@ExtendWith(MockitoExtension.class)
public class UserControllerUnitTestDy {
	@Mock
	UserService userService;
	@Mock
	PointService pointService;
	
	@InjectMocks
	UserController userController;
	
	MockMvc mockMvc;
	User mockedUser;
	UserDto mockedDto;
	
	Gson gson;
	
	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders
				.standaloneSetup(userController)
				.setControllerAdvice(new ExceptionHandlerAdvice())
				.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
				.setValidator(mock(Validator.class))
				.build();
		mockedUser = User.builder()
				.username("test")
				.password("password")
				.email("test@test.com")
				.nickname("nickname")
				.build();
		mockedDto = UserMapper.toDto(mockedUser);
		gson = new Gson();
	}
	
	@Nested
	class GetMyInfo{
		@Test
		void success() throws Exception{
			// given 
			doReturn(mockedDto).when(userService).getUserByUsername(any());
			
			// when
			mockMvc.perform(get("/api/user"))
			// then
			.andExpectAll(
					status().isOk(),
					jsonPath("$.code").value("OK"),
					jsonPath("$.data.username").value("test")
					)
			;
		}
		@Test
		void fail() throws Exception {
			// given
			doThrow(new BusinessLogicException(BusinessLogicError.NOTFOUND_USER))
			.when(userService).getUserByUsername(any());
			
			// when
			mockMvc.perform(get("/api/user"))
			// then
			.andExpect(status().isNotFound());
		}
	}
	@Nested
	class UpdateMyInfo{
		@Test
		void success() throws Exception{
			// given
			doReturn(mockedDto).when(userService).changeNickname(any(), eq(mockedDto.getNickname()));
			
			var request = UserUpdateRequest.builder()
					.nickname(mockedDto.getNickname())
					.build();
			// when
			mockMvc.perform(
					patch("/api/user")
					.content(gson.toJson(request))
					.contentType(MediaType.APPLICATION_JSON)
					)
			// then
			.andExpectAll(
					status().isOk(),
					jsonPath("$.code").value("OK"),
					jsonPath("$.data.nickname").value(mockedDto.getNickname())
					)
			;
		}
		
		@Test
		void fail() throws Exception{
			// given
			doThrow(new BusinessLogicException(BusinessLogicError.DUPLICATED_NICKNAME))
			.when(userService).changeNickname(any(), eq(mockedDto.getNickname()));
			var request = UserUpdateRequest.builder()
					.nickname(mockedDto.getNickname())
					.build();
			
			// when
			mockMvc.perform(
					patch("/api/user")
					.content(gson.toJson(request))
					.contentType(MediaType.APPLICATION_JSON)
					)
			// then
			.andExpectAll(
					status().isBadRequest(),
					jsonPath("$.code").value("12002")
					);
		}
	}
	
	@Nested
	class Withdraw{
		@Test
		void success() throws Exception{
			// given
			mockedDto.setWithdraw(true);
			doReturn(mockedDto).when(userService).changeWithdraw(any(), eq(true));
			// when
			mockMvc.perform(patch("/api/user/withdraw"))
			
			// then
			.andExpectAll(
					status().isOk(),
					jsonPath("$.data.withdraw").value(true)
					)
			;
		}
	}
	
	@Nested
	class GetMyPoints {
		@Test
		void success() throws Exception{
			// given
			var point = Point.builder()
					.id(1)
					.user(mockedUser)
					.updatedAt(LocalDateTime.now())
					.type(PointType.EARN)
					.diff(300)
					.build();
			var pointDto = PointMapper.toDto(point);
			var pointDtoList = List.of(pointDto, pointDto, pointDto);
			var pageRequest = PageRequest.of(0, 10, Sort.by("id").descending());
			var pointDtoPage = new PageImpl<>(pointDtoList, pageRequest, 3);
			
			doReturn(pointDtoPage).when(pointService).pointsByUsername(any(), eq(pageRequest));
			
			// when
			mockMvc.perform(get("/api/user/point"))
			
			// then
			.andExpectAll(
					status().isOk(),
					jsonPath("$.data.totalElements").value(3),
					jsonPath("$.data.content").isArray(),
					jsonPath("$.data.content.size()").value(3),
					jsonPath("$.data.content[0].id").value(1),
					jsonPath("$.data.content[0].username").value(mockedUser.getUsername()),
					jsonPath("$.data.content[0].updatedAt").exists(),
					jsonPath("$.data.content[0].type").isString(),
					jsonPath("$.data.content[0].diff").value(300)
					)
			;
			
		}
	}
	
}
