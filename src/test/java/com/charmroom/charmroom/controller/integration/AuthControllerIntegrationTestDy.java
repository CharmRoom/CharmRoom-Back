package com.charmroom.charmroom.controller.integration;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@TestPropertySource(properties = {"spring.config.location = classpath:application-test.yml"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class AuthControllerIntegrationTestDy {
	@Autowired
	MockMvc mockMvc;
	
	MockMultipartFile imageFile = new MockMultipartFile("image", "test.png", MediaType.IMAGE_PNG_VALUE, "test".getBytes());
	String username = "test";
	String password = "password";
	String email = "test@test.com";
	String nickname = "nickname";
	
	@Nested
	class Signup {
		String url = "/api/auth/signup";
		@Test
		void success() throws Exception {
			// given
			
			// when
			mockMvc.perform(multipart(url)
					.file(imageFile)
					.param("username", username)
					.param("password", password)
					.param("rePassword", password)
					.param("email", email)
					.param("nickname", nickname)
					)
			// then
			.andExpectAll(
					status().isCreated()
					,jsonPath("$.code").value("CREATED")
					,jsonPath("$.data.username").value(username)
					,jsonPath("$.data.nickname").value(nickname)
					,jsonPath("$.data.email").value(email)
					)
			;
		}
		@Test
		void successWithoutImage() throws Exception {
			// given
			// when
			mockMvc.perform(multipart(url)
					.param("username", username)
					.param("password", password)
					.param("rePassword", password)
					.param("email", email)
					.param("nickname", nickname)
					)
			// then
			.andExpectAll(
					status().isCreated()
					,jsonPath("$.code").value("CREATED")
					,jsonPath("$.data.username").value(username)
					,jsonPath("$.data.nickname").value(nickname)
					,jsonPath("$.data.email").value(email)
					)
			;
		}
		@Test
		void failPasswordValidation() throws Exception{
			// given
			// when
			mockMvc.perform(multipart(url)
					.param("username", username)
					.param("password", "1234")
					.param("rePassword", "5678")
					.param("email", email)
					.param("nickname", nickname)
					)
			// then
			.andExpectAll(
					status().isNotAcceptable()
					,jsonPath("$.code").value("INVALID")
					,jsonPath("$.data.signupRequestDto", containsString("password"))
					)
			;
		}
		
		@Test
		void failDuplicatedUsername() throws Exception {
			// given
			mockMvc.perform(multipart(url)
					.param("username", username)
					.param("password", password)
					.param("rePassword", password)
					.param("email", email)
					.param("nickname", nickname)
					);
			
			// when
			mockMvc.perform(multipart(url)
					.file(imageFile)
					.param("username", username)
					.param("password", password)
					.param("rePassword", password)
					.param("email", "a" + email)
					.param("nickname", "a" + nickname)
					)
			// then
			.andExpectAll(
					status().isNotAcceptable()
					,jsonPath("$.code").value("INVALID")
					,jsonPath("$.data.username", containsString("Duplicated"))
					)
			;
		}
		
		@Test
		void failDuplicatedEmail() throws Exception {
			// given
			mockMvc.perform(multipart(url)
					.param("username", username)
					.param("password", password)
					.param("rePassword", password)
					.param("email", email)
					.param("nickname", nickname)
					);
			
			// when
			mockMvc.perform(multipart(url)
					.file(imageFile)
					.param("username", "a" + username)
					.param("password", password)
					.param("rePassword", password)
					.param("email", email)
					.param("nickname", "a" + nickname)
					)
			// then
			.andExpectAll(
					status().isNotAcceptable()
					,jsonPath("$.code").value("INVALID")
					,jsonPath("$.data.email", containsString("Duplicated"))
					)
			;
		}
		
		@Test
		void failDuplicatedNickname() throws Exception{
			// given
			mockMvc.perform(multipart(url)
					.param("username", username)
					.param("password", password)
					.param("rePassword", password)
					.param("email", email)
					.param("nickname", nickname)
					);
			// when
			mockMvc.perform(multipart(url)
					.file(imageFile)
					.param("username", "a" + username)
					.param("password", password)
					.param("rePassword", password)
					.param("email", "a" +  email)
					.param("nickname", nickname)
					)
			// then
			.andExpectAll(
					status().isNotAcceptable()
					,jsonPath("$.code").value("INVALID")
					,jsonPath("$.data.nickname", containsString("Duplicated"))
					)
			;
		}
		
		@Test
		void failMultipleValidationError() throws Exception{
			// given
			mockMvc.perform(multipart(url)
					.param("username", username)
					.param("password", password)
					.param("rePassword", password)
					.param("email", email)
					.param("nickname", nickname)
					);
			// when
			mockMvc.perform(multipart(url)
					.file(imageFile)
					.param("username", username)
					.param("password", "1234")
					.param("rePassword", "5678")
					.param("email", email)
					.param("nickname", nickname)
					)
			// then
			.andExpectAll(
					status().isNotAcceptable()
					,jsonPath("$.code").value("INVALID")
					,jsonPath("$.data.signupRequestDto", containsString("password"))
					,jsonPath("$.data.username", containsString("Duplicated"))
					,jsonPath("$.data.email", containsString("Duplicated"))
					,jsonPath("$.data.nickname", containsString("Duplicated"))
					)
			;
		}
	}
	
	@Nested
	class Login {
		String url = "/api/auth/login";
		
		@BeforeEach
		void setup() throws Exception {
			// given
			mockMvc.perform(multipart("/api/auth/signup")
					.param("username", username)
					.param("password", password)
					.param("rePassword", password)
					.param("email", email)
					.param("nickname", nickname)
					);
		}
		
		@Test
		void success() throws Exception {
			// when
			mockMvc.perform(post(url)
					.param("username", username)
					.param("password", password)
					)
			// then
			.andExpectAll(
					status().isOk()
					,header().exists(HttpHeaders.AUTHORIZATION)
					)
			;
		}
		
		@Test
		void fail() throws Exception {
			// when
			mockMvc.perform(post(url)
					.param("username", username)
					.param("password", password + "1")
					)
			// then
			.andExpect(
					status().isUnauthorized()
					)
			;
		}
	}
	
	
}
