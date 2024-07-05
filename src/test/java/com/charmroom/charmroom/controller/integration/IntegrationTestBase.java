package com.charmroom.charmroom.controller.integration;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.entity.enums.UserLevel;
import com.charmroom.charmroom.repository.UserRepository;

@TestPropertySource(properties = {"spring.config.location = classpath:application-test.yml"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class IntegrationTestBase {
	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	UserRepository userRepository;
	
	User charmroomUser;
	User charmroomAdmin;
	@Target({ ElementType.METHOD, ElementType.TYPE })
	@Retention(RetentionPolicy.RUNTIME)
	@Inherited
	@WithUserDetails(
			value = "test", 
			setupBefore = TestExecutionEvent.TEST_EXECUTION,
			userDetailsServiceBeanName = "customUserDetailsService") 
	@interface WithCharmroomUserDetails {}
	@Target({ ElementType.METHOD, ElementType.TYPE })
	@Retention(RetentionPolicy.RUNTIME)
	@Inherited
	@WithUserDetails(
			value = "admin", 
			setupBefore = TestExecutionEvent.TEST_EXECUTION,
			userDetailsServiceBeanName = "customUserDetailsService")
	@interface WithCharmroomAdminDetails{}
	
	@AfterAll
	static void cleanUp(
			@Value("${charmroom.upload.image.path}") String imageUploadPath,
			@Value("${charmroom.upload.attachment.path}") String attachmentUploadPath) throws IOException {
		FileUtils.cleanDirectory(new File(imageUploadPath));
		FileUtils.cleanDirectory(new File(attachmentUploadPath));
	}
	
	@BeforeEach
	void setup() throws Exception {
		charmroomUser = User.builder()
				.username("test")
				.password("")
				.email("test@test.com")
				.nickname("test")
				.level(UserLevel.ROLE_BASIC)
				.build();
		charmroomUser = userRepository.save(charmroomUser);
		charmroomAdmin = User.builder()
				.username("admin")
				.password("")
				.email("admin@admin.com")
				.nickname("admin")
				.level(UserLevel.ROLE_ADMIN)
				.build();
		charmroomAdmin = userRepository.save(charmroomAdmin);
	}
}
