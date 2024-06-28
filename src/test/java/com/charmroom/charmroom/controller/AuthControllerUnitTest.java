package com.charmroom.charmroom.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import com.charmroom.charmroom.controller.api.AuthController;
import com.charmroom.charmroom.dto.business.UserDto;
import com.charmroom.charmroom.dto.business.UserMapper;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.exception.ExceptionHandlerAdvice;
import com.charmroom.charmroom.service.UserService;



@ExtendWith(MockitoExtension.class)
public class AuthControllerUnitTest {
	@Mock
	UserService userService;
	@InjectMocks
	AuthController authController;
	
	MockMvc mockMvc;
	User mockedUser;
	UserDto mockedDto;
	
	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders
				.standaloneSetup(authController)
				.setControllerAdvice(new ExceptionHandlerAdvice())
				.setValidator(mock(Validator.class))
				.build();
		mockedUser = User.builder()
				.username("test")
				.password("password")
				.email("test@test.com")
				.nickname("nickname")
				.build();
		mockedDto = UserMapper.toDto(mockedUser);
	}
	
	@Nested
	class signupTest {
		@Test
		void success() throws Exception {
			// given
			MockMultipartFile imageFile = new MockMultipartFile("image", "test.png", MediaType.IMAGE_PNG_VALUE, "test".getBytes());
			doReturn(mockedDto).when(userService).create(
					any(UserDto.class),
					eq(imageFile));
			
			// when
			mockMvc.perform(
					multipart("/api/auth/signup")
					.file(imageFile)
					.param("username", mockedUser.getUsername())
					.param("password", mockedUser.getPassword())
					.param("rePassword", mockedUser.getPassword())
					.param("email", mockedUser.getEmail())
					.param("nickname", mockedUser.getNickname())
					)
			// then
			.andExpectAll(
					status().isCreated(),
					jsonPath("$.code").value("CREATED"),
					jsonPath("$.data.username").value(mockedUser.getUsername()),
					jsonPath("$.data.nickname").value(mockedUser.getNickname()),
					jsonPath("$.data.email").value(mockedUser.getEmail())
					)
			;
		}
		
		@Test
		void successWithoutImage() throws Exception {
			// given
			doReturn(mockedDto).when(userService).create(
					any(UserDto.class),
					eq(null)
					);
			
			// when
			mockMvc.perform(
					multipart("/api/auth/signup")
					
					.param("username", mockedUser.getUsername())
					.param("password", mockedUser.getPassword())
					.param("rePassword", mockedUser.getPassword())
					.param("email", mockedUser.getEmail())
					.param("nickname", mockedUser.getNickname())
					)
			// then
			.andExpectAll(
					status().isCreated(),
					jsonPath("$.code").value("CREATED"),
					jsonPath("$.data.username").value(mockedUser.getUsername()),
					jsonPath("$.data.nickname").value(mockedUser.getNickname()),
					jsonPath("$.data.email").value(mockedUser.getEmail())
					)
			;
		}
		
		@Test
		void fail() throws Exception {
			doThrow(new BusinessLogicException(BusinessLogicError.DUPLICATED_USERNAME))
				.when(userService)
				.create(any(UserDto.class),eq(null));
			mockMvc.perform(
					multipart("/api/auth/signup")
					
					.param("username", mockedUser.getUsername())
					.param("password", mockedUser.getPassword())
					.param("rePassword", mockedUser.getPassword())
					.param("email", mockedUser.getEmail())
					.param("nickname", mockedUser.getNickname())
					)
			.andExpectAll(
					status().isBadRequest(),
					jsonPath("$.code").value("12000")
					);
		}
	}
}
