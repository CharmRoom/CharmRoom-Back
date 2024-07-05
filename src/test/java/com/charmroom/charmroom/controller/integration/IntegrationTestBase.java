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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.charmroom.charmroom.entity.Image;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.entity.enums.UserLevel;
import com.charmroom.charmroom.repository.ImageRepository;
import com.charmroom.charmroom.repository.UserRepository;
import com.charmroom.charmroom.util.CharmroomUtil;
import com.google.gson.Gson;

@TestPropertySource(properties = {"spring.config.location = classpath:application-test.yml"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional(propagation = Propagation.SUPPORTS)
public class IntegrationTestBase {
	@Autowired
	MockMvc mockMvc;
	Gson gson;
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ImageRepository imageRepository;
	
	@Autowired
	private CharmroomUtil.Upload uploadUtil;
	
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
		MultipartFile file = new MockMultipartFile("image", "profile.png", MediaType.IMAGE_PNG_VALUE, "test".getBytes());
		Image profile1 = uploadUtil.buildImage(file);
		Image profile2 = uploadUtil.buildImage(file);
		profile1 = imageRepository.save(profile1);
		profile2 = imageRepository.save(profile2);
		
		charmroomUser = User.builder()
				.username("test")
				.password("")
				.email("test@test.com")
				.nickname("test")
				.level(UserLevel.ROLE_BASIC)
				.image(profile1)
				.build();
		charmroomUser = userRepository.save(charmroomUser);
		charmroomAdmin = User.builder()
				.username("admin")
				.password("")
				.email("admin@admin.com")
				.nickname("admin")
				.level(UserLevel.ROLE_ADMIN)
				.image(profile2)
				.build();
		charmroomAdmin = userRepository.save(charmroomAdmin);
		gson = new Gson();
	}
}
