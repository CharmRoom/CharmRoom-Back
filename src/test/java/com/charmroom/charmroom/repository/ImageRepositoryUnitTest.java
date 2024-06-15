package com.charmroom.charmroom.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import com.charmroom.charmroom.entity.Image;

@DataJpaTest
@TestPropertySource(properties = {"spring.config.location = classpath:application-test.yml"})
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Image Repository 단위 테스트")
public class ImageRepositoryUnitTest {
	
	@Autowired
	private ImageRepository imageRepository;
	
	private Image image = buildImage();
	
	private Image buildImage() {
		return Image.builder()
				.path("/example/example")
				.build();
	}
	
	@Nested
	@DisplayName("CREATE")
	class CreateTest {
		@Test
		public void success() {
			// given
			// when
			Image savedImage = imageRepository.save(image);
			
			// then
			assertThat(savedImage).isNotNull();
			assertThat(savedImage.getPath()).isEqualTo(image.getPath());
			
		}
	}
	
	@Nested
	@DisplayName("READ")
	class ReadTest {
		@Test
		public void success() {
			// given
			Image savedImage = imageRepository.save(image);
			
			// when
			Image foundImage = imageRepository.findById(savedImage.getId()).orElseThrow();
			
			// then
			assertThat(foundImage).isNotNull();
			assertThat(foundImage.getPath()).isEqualTo(image.getPath());
		}
		
		@Test
		public void fail() {
			// given
			imageRepository.save(image);
			
			// when
			var result = imageRepository.findById(12345);
			
			// then
			assertThat(result).isNotPresent();
		}
	}
	
	@Nested
	@DisplayName("UPDATE")
	class UpdateTest {
		@Test
		public void success() {
			// given
			Image savedImage = imageRepository.save(image);
			
			// when
			String newPath = "new/path";
			savedImage.update(newPath);
			
			// then
			Image foundImage = imageRepository.findById(savedImage.getId()).get();
			assertThat(foundImage).isNotNull();
			assertThat(foundImage.getPath()).isEqualTo(newPath);
		}
	}
	
	@Nested
	@DisplayName("DELETE")
	class DeleteTest {
		@Test
		public void success() {
			imageRepository.save(image);
			
			// when
			imageRepository.delete(image);
			var imageList = imageRepository.findAll();
			
			// then
			assertThat(imageList).isNotNull();
			assertThat(imageList).isEmpty();
		}
	}
}
