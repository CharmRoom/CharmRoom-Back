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

import com.charmroom.charmroom.entity.Image;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.repository.ImageRepository;

@ExtendWith(MockitoExtension.class)
public class ImageServiceUnitTest {
	@Mock
	private ImageRepository imageRepository;
	@InjectMocks
	private ImageService imageService;
	
	private Image image;
	
	@BeforeEach
	void setup() {
		image = Image.builder()
				.id(1)
				.build();
	}
	
	@Nested
	class LoadById{
		@Test
		void success() {
			// given
			doReturn(Optional.of(image)).when(imageRepository).findById(image.getId());
			
			// when
			var loaded = imageService.loadById(image.getId());
			
			// then
			assertThat(loaded).isNotNull();
		}
		
		@Test
		void fail() {
			// given
			doReturn(Optional.empty()).when(imageRepository).findById(image.getId());
			
			// when
			var thrown = assertThrows(BusinessLogicException.class, () -> {
				imageService.loadById(image.getId());
			});
			
			// then
			assertThat(thrown.getError()).isEqualTo(BusinessLogicError.NOTFOUND_IMAGE);
		}
	}
}
