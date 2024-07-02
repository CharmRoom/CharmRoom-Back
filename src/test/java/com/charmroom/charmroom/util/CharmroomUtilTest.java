package com.charmroom.charmroom.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;

import com.charmroom.charmroom.entity.Article;
import com.charmroom.charmroom.entity.Attachment;
import com.charmroom.charmroom.entity.Image;
import com.charmroom.charmroom.entity.enums.AttachmentType;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;

@SpringBootTest
@TestPropertySource(properties = {"spring.config.location = classpath:application-test.yml"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CharmroomUtilTest {
	@Nested
	@TestClassOrder(ClassOrderer.OrderAnnotation.class)
	class Upload{
		
		@Autowired
		private CharmroomUtil.Upload uploadUtil;
		private static String attachmentUploadPath;
		private static String imageUploadPath;
		
		@AfterAll
		static void postTest() throws IOException {
			File imageDir = new File(imageUploadPath);
			File attachmentDir = new File(attachmentUploadPath);
			FileUtils.cleanDirectory(imageDir);
			FileUtils.cleanDirectory(attachmentDir);
			FileUtils.deleteDirectory(imageDir);
			FileUtils.deleteDirectory(attachmentDir);
		}
		@Nested
		@Order(1)
		class Construct{
			@Test
			void success() {
				// given (autowired construct)
				
				// when
				attachmentUploadPath = uploadUtil.getAttachmentUploadPath();
				imageUploadPath = uploadUtil.getImageUploadPath();
				
				// then
				assertThat(attachmentUploadPath).isNotNull();
				assertThat(imageUploadPath).isNotNull();
				
				File attachmentDir = new File(attachmentUploadPath);
				File imageDir = new File(imageUploadPath);
				assertThat(attachmentDir.exists()).isTrue();
				assertThat(imageDir.exists()).isTrue();
			}
		}
		@Nested
		@Order(2)
		class BuildImage{
			@Test
			void success() {
				// given
				MockMultipartFile imageFile = new MockMultipartFile("file", "test.png", "image/png", "test".getBytes());
				
				// when
				Image image = uploadUtil.buildImage(imageFile);
				
				// then
				assertThat(image).isNotNull();
				assertThat(image.getOriginalName()).isEqualTo("test.png");
				String path = uploadUtil.getRealPath(image);
				File savedFile = new File(path);
				assertThat(savedFile.exists()).isTrue();
				assertThat(savedFile.isFile()).isTrue();
			}
			@Test
			void fail() {
				// given
				MockMultipartFile imageFile = new MockMultipartFile("file", "test.png", "video/mp4", "test".getBytes());
				
				// when
				var thrown = assertThrows(BusinessLogicException.class, () -> uploadUtil.buildImage(imageFile));
				
				// then
				assertThat(thrown.getError()).isEqualTo(BusinessLogicError.FILE_NOT_IMAGE);
			}
		}
		
		@Nested
		@Order(3)
		class BuildAttachment{
			@Test
			void success() {
				// given
				MockMultipartFile imageFile = new MockMultipartFile("file", "test1.png", "image/png", "test".getBytes());
				MockMultipartFile videoFile = new MockMultipartFile("file", "test2.mp4", "video/mp4", "test".getBytes());
				MockMultipartFile etcFile = new MockMultipartFile("file", "test3.html", "text/html", "test".getBytes());
				Article article = Article.builder()
						.build();
				// when
				Attachment image = uploadUtil.buildAttachment(imageFile, article);
				Attachment video = uploadUtil.buildAttachment(videoFile, article);
				Attachment etc = uploadUtil.buildAttachment(etcFile, article);
				
				// then
				assertThat(image).isNotNull();
				assertThat(video).isNotNull();
				assertThat(etc).isNotNull();
				assertThat(image.getOriginalName()).isEqualTo("test1.png");
				assertThat(video.getOriginalName()).isEqualTo("test2.mp4");
				assertThat(etc.getOriginalName()).isEqualTo("test3.html");
				assertThat(image.getType()).isEqualTo(AttachmentType.IMAGE);
				assertThat(video.getType()).isEqualTo(AttachmentType.VIDEO);
				assertThat(etc.getType()).isEqualTo(AttachmentType.ETC);
				
				File savedImage = new File(uploadUtil.getRealPath(image));
				assertThat(savedImage.exists()).isTrue();
				assertThat(savedImage.isFile()).isTrue();
				File savedVideo = new File(uploadUtil.getRealPath(video));
				assertThat(savedVideo.exists()).isTrue();
				assertThat(savedVideo.isFile()).isTrue();
				File savedEtc = new File(uploadUtil.getRealPath(etc));
				assertThat(savedEtc.exists()).isTrue();
				assertThat(savedEtc.isFile()).isTrue();
			}
		}
		
		@Nested
		@Order(4)
		class Delete{
			@Test
			void success() {
				// given
				MockMultipartFile imageFile = new MockMultipartFile("file", "test1.png", "image/png", "test".getBytes());
				MockMultipartFile attachmentFile = new MockMultipartFile("file", "test3.html", "text/html", "test".getBytes());
				
				Image image = uploadUtil.buildImage(imageFile);
				Article article = Article.builder()
						.build();
				Attachment attachment = uploadUtil.buildAttachment(attachmentFile, article);
				
				// when
				uploadUtil.deleteFile(image);
				uploadUtil.deleteFile(attachment);
				
				// then
				File deletedImage = new File(uploadUtil.getRealPath(image));
				assertThat(deletedImage.exists()).isFalse();
				File deletedAttachment = new File(uploadUtil.getRealPath(attachment));
				assertThat(deletedAttachment.exists()).isFalse();
			}
			
			@Test
			void fail() {
				// given
				MockMultipartFile imageFile = new MockMultipartFile("file", "test1.png", "image/png", "test".getBytes());
				Image image = uploadUtil.buildImage(imageFile);
				
				// when
				uploadUtil.deleteFile(image);
				var thrown = assertThrows(BusinessLogicException.class, () -> {
					uploadUtil.deleteFile(image);
				});
				
				// then
				assertThat(thrown.getError()).isEqualTo(BusinessLogicError.DELETE_FAIL);
			}
		}
	}
}
