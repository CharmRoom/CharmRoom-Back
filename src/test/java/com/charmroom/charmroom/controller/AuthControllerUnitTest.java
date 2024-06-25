package com.charmroom.charmroom.controller;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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
import com.charmroom.charmroom.entity.User;
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
	}
	
	@Nested
	class signupTest {
		@Test
		void success() throws Exception {
			// given
			MockMultipartFile imageFile = new MockMultipartFile("image", "test.png", MediaType.IMAGE_PNG_VALUE, "test".getBytes());
			doReturn(mockedUser).when(userService).create(
					mockedUser.getUsername(),
					mockedUser.getEmail(),
					mockedUser.getNickname(),
					mockedUser.getPassword(),
					imageFile);
			
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
			doReturn(mockedUser).when(userService).create(
					mockedUser.getUsername(),
					mockedUser.getEmail(),
					mockedUser.getNickname(),
					mockedUser.getPassword(),
					null
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
	}
}
