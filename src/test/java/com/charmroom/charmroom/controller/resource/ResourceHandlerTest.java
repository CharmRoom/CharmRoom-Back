package com.charmroom.charmroom.controller.resource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import com.charmroom.charmroom.controller.integration.IntegrationTestBase;
import com.charmroom.charmroom.controller.integration.IntegrationTestBase.WithCharmroomUserDetails;
import com.charmroom.charmroom.entity.Article;
import com.charmroom.charmroom.entity.Attachment;
import com.charmroom.charmroom.entity.Image;
import com.charmroom.charmroom.util.CharmroomUtil;


@WithCharmroomUserDetails
public class ResourceHandlerTest extends IntegrationTestBase {
	@Autowired
	CharmroomUtil.Upload uploadUtil;
	
	@Nested
	class ImageResourceHandler{
		// given
		MockMultipartFile mockedImage = new MockMultipartFile("image", "image.png", 
				MediaType.IMAGE_PNG_VALUE, "test".getBytes());
		Image image = uploadUtil.buildImage(mockedImage);
		
		@Test
		void success() throws Exception {
			// when
			// then
			mockMvc.perform(get(image.getPath()))
			.andExpect(status().isOk())
			;
		}
	}
	@Nested
	class AttachmentResourceHandler{
		// given
		MockMultipartFile mockedAttach = new MockMultipartFile("attach", "attach.html", 
				MediaType.TEXT_HTML_VALUE, "test".getBytes());
		Article article = Article.builder()
				.title("")
				.body("")
				.build();
		Attachment attachment = uploadUtil.buildAttachment(mockedAttach, article);
		
		@Test
		void success() throws Exception {
			// when
			mockMvc.perform(get(attachment.getPath()))
			// then
			.andExpect(status().isOk());
		}
	}
}
