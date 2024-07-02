package com.charmroom.charmroom.controller.unit;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import com.charmroom.charmroom.controller.api.DownloadController;
import com.charmroom.charmroom.dto.business.AttachmentDto;
import com.charmroom.charmroom.dto.business.ImageDto;
import com.charmroom.charmroom.exception.ExceptionHandlerAdvice;
import com.charmroom.charmroom.service.AttachmentService;
import com.charmroom.charmroom.service.ImageService;

@ExtendWith(MockitoExtension.class)
public class DownloadControllerUnitTestDy {
	@Mock ImageService imageService;
	@Mock AttachmentService attachmentService;
	@InjectMocks DownloadController downloadController;
	
	MockMvc mockMvc;
	ImageDto mockedImage;
	AttachmentDto mockedAttach;
	Resource mockedResource;
	
	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders
				.standaloneSetup(downloadController)
				.setControllerAdvice(new ExceptionHandlerAdvice())
				.setValidator(mock(Validator.class))
				.build();
		mockedResource = new ByteArrayResource("test".getBytes());
		
		mockedImage = ImageDto.builder()
				.id(1)
				.path("path")
				.originalName("image.png")
				.resource(mockedResource)
				.build();
		mockedAttach = AttachmentDto.builder()
				.id(2)
				.path("path")
				.originalName("attach.mp4")
				.build();
	}
	
	@Nested
	class DownloadImage{
		@Test
		void success() throws Exception {
			// given
			doReturn(mockedImage).when(imageService).loadById(mockedImage.getId());
			
			// when
			mockMvc.perform(get("/download/image/" + mockedImage.getId()))
			
			// then
			.andExpectAll(
					status().isOk(),
					header().string(HttpHeaders.CONTENT_DISPOSITION
							, "attachment; filename=\"" + mockedImage.getOriginalName() + "\";")
					)
			;
		}
	}
	@Nested
	class DownloadAttachment{
		@Test
		void success() throws Exception{
			// given
			doReturn(mockedAttach).when(attachmentService).loadById(mockedAttach.getId());
			
			// when
			mockMvc.perform(get("/download/attachment/" + mockedAttach.getId()))
			
			//then
			.andExpectAll(
					status().isOk(),
					header().string(HttpHeaders.CONTENT_DISPOSITION
							, "attachment; filename=\"" + mockedAttach.getOriginalName() + "\";")
					)
			;
		}
	}
	
}
