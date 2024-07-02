package com.charmroom.charmroom.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

import com.charmroom.charmroom.entity.Attachment;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.repository.AttachmentRepository;
import com.charmroom.charmroom.util.CharmroomUtil;

@ExtendWith(MockitoExtension.class)
public class AttachmentServiceUnitTest {
	@Mock
	private AttachmentRepository attachmentRepository;
	@Mock
	private CharmroomUtil.Upload uploadUtil;
	@InjectMocks
	private AttachmentService attachmentService;
	
	private Attachment attachment;
	private Resource mockedResource;
	
	@BeforeEach
	void setup() {
		attachment = Attachment.builder()
				.id(1)
				.build();
	}
	
	@Nested
	class LoadById{
		@Test
		void success() {
			// given
			doReturn(Optional.of(attachment)).when(attachmentRepository).findById(attachment.getId());
			doReturn(mockedResource).when(uploadUtil).toResource(attachment);
			// when
			var loaded = attachmentService.loadById(attachment.getId());
			
			// then
			assertThat(loaded).isNotNull();
		}
		
		@Test
		void fail() {
			// given
			doReturn(Optional.empty()).when(attachmentRepository).findById(attachment.getId());
			
			// when
			var thrown = assertThrows(BusinessLogicException.class, () -> {
				attachmentService.loadById(attachment.getId());
			});
			
			// then
			assertThat(thrown.getError()).isEqualTo(BusinessLogicError.NOTFOUND_ATTACHMENT);
		}
	}
}
