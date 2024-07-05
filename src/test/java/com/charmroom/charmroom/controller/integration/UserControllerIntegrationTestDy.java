package com.charmroom.charmroom.controller.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@TestPropertySource(properties = {"spring.config.location = classpath:application-test.yml"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class UserControllerIntegrationTestDy extends IntegrationTestBase {
	
	
	@Nested
	class GetMyInfo{
		@Test
		@WithCharmroomUserDetails
		void success() throws Exception {
			mockMvc.perform(get("/api/user"))
			.andExpectAll(
					status().isOk()
					,jsonPath("$.data.username").value(charmroomUser.getUsername())
					,jsonPath("$.data.email").value(charmroomUser.getEmail())
					,jsonPath("$.data.nickname").value(charmroomUser.getNickname())
					,jsonPath("$.data.withdraw").value(charmroomUser.isWithdraw())
					,jsonPath("$.data.level").value(charmroomUser.getLevel().toString())
					);
		}
	}
}
