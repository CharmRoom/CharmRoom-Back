package com.charmroom.charmroom.controller.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import com.charmroom.charmroom.controller.integration.IntegrationTestBase.WithCharmroomUserDetails;
import com.charmroom.charmroom.entity.Article;
import com.charmroom.charmroom.entity.Attachment;
import com.charmroom.charmroom.entity.Image;
import com.charmroom.charmroom.repository.ArticleRepository;
import com.charmroom.charmroom.repository.AttachmentRepository;
import com.charmroom.charmroom.repository.ImageRepository;
import com.charmroom.charmroom.util.CharmroomUtil;

@WithCharmroomUserDetails
public class DownloadControllerIntegrationTestDy extends IntegrationTestBase {
	@Autowired
	CharmroomUtil.Upload uploadUtil;
	
	String urlPrefix = "/download";
	
	@Nested
	class DownloadImgae{
		@Autowired
		ImageRepository imageRepository;
		String url = urlPrefix + "/image/";
		MockMultipartFile mockedImage = new MockMultipartFile("image", "image.png", 
				MediaType.IMAGE_PNG_VALUE, "test".getBytes());
		Image image = uploadUtil.buildImage(mockedImage);
		
		@Test
		void success() throws Exception {
			// given
			image = imageRepository.save(image);
			
			// when
			mockMvc.perform(get(url + image.getId()))
			
			// then
			.andExpectAll(
					status().isOk()
					,header().string(HttpHeaders.CONTENT_DISPOSITION
							, "attachment; filename=\"" + image.getOriginalName() + "\";")
					)
			;
		}
		
		@Test
		void failByNotFoundImage() throws Exception {
			// given
			// when
			mockMvc.perform(get(url + "12345"))
			
			// then
			.andExpectAll(
					status().isNotFound()
					,jsonPath("$.code").value("08100")
					);
		}
	}
	
	@Nested
	class DownloadAttachment {
		@Autowired
		AttachmentRepository attachmentRepository;
		@Autowired
		ArticleRepository articleRepository;
		
		String url = urlPrefix + "/attachment/";
		MockMultipartFile mockedAttach = new MockMultipartFile("attach", "attach.html", 
				MediaType.TEXT_HTML_VALUE, "test".getBytes());
		
		@Test
		void success() throws Exception {
			// given
			Article article = Article.builder()
					.title("")
					.body("")
					.build();
			article = articleRepository.save(article);
			Attachment attachment = uploadUtil.buildAttachment(mockedAttach, article);
			attachment = attachmentRepository.save(attachment);
			article.getAttachmentList().add(attachment);
			
			// when
			mockMvc.perform(get(url + attachment.getId()))
			// then
			.andExpectAll(
					status().isOk()
					,header().string(HttpHeaders.CONTENT_DISPOSITION
							, "attachment; filename=\"" + attachment.getOriginalName() + "\";")
					)
			;
		}
		
		@Test
		void failByNotFoundAttachment() throws Exception {
			// given
			// when
			mockMvc.perform(get(url + "12345"))
			
			// then
			.andExpectAll(
					status().isNotFound()
					,jsonPath("$.code").value("03100")
					);
		}
	}
}
