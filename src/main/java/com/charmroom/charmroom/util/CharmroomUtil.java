package com.charmroom.charmroom.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.charmroom.charmroom.entity.Article;
import com.charmroom.charmroom.entity.Attachment;
import com.charmroom.charmroom.entity.Image;
import com.charmroom.charmroom.entity.enums.AttachmentType;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class CharmroomUtil {
	@Component
	@Getter
	public class Upload {
		private String imageUploadPath;
		private String attachmentUploadPath;
		
		public Upload(
				@Value("${charmroom.upload.image.path}") String imageUploadPath,
				@Value("${charmroom.upload.attachment.path}") String attachmentUploadPath
				) {
			this.imageUploadPath = imageUploadPath;
			this.attachmentUploadPath = attachmentUploadPath;
			setPathReady(this.imageUploadPath);
			setPathReady(this.attachmentUploadPath);
		}
		
		public Image buildImage(MultipartFile image) {
			if (!Objects.requireNonNull(image.getContentType()).startsWith("image"))
				throw new BusinessLogicException(BusinessLogicError.FILE_NOT_IMAGE);
			String originalName = image.getOriginalFilename();
			String fullPath = newFileName(imageUploadPath, image);
			uploadFile(fullPath, image);
			return Image.builder()
					.path(fullPath)
					.originalName(originalName)
					.build();
		}
		
		public Attachment buildAttachment(MultipartFile attachment, Article article) {
			String originalName = attachment.getOriginalFilename();
			String fullPath = newFileName(attachmentUploadPath, attachment);
			uploadFile(fullPath, attachment);
			AttachmentType type = AttachmentType.ETC;
			if (attachment.getContentType().startsWith("image"))
				type = AttachmentType.IMAGE;
			if (attachment.getContentType().startsWith("video"))
				type = AttachmentType.VIDEO;
			
			return Attachment.builder()
					.article(article)
					.path(fullPath)
					.originalName(originalName)
					.type(type)
					.build();
		}
		
		public void deleteImageFile(Image image) {
			unlinkFile(image.getPath());
		}
		
		public void deleteAttachmentFile(Attachment attachment) {
			unlinkFile(attachment.getPath());
		}
		
		private void unlinkFile(String path) {
			if ( ! new File(path).delete() )
				throw new BusinessLogicException(BusinessLogicError.DELETE_FAIL);
		}
		
		private String newFileName(String path, MultipartFile file) {
			String original = file.getOriginalFilename();
			String ext = original.substring(original.lastIndexOf("."));
			String uuid = UUID.randomUUID().toString();
			
			return path + File.separator + uuid + ext;
		}
		private void uploadFile(String path, MultipartFile file) {
			try {
				Path savePath = Paths.get(path);
				file.transferTo(savePath);
			} catch (IllegalStateException | IOException e) {
				log.info("File save failed: " + path);
			}
		}
		
		private void setPathReady(String path) {
			File directory = new File(path);
			if (!directory.exists()) {
				if (!directory.mkdirs()) 
					throw new BusinessLogicException(BusinessLogicError.MKDIR_FAIL);
				log.info("mkdir: " + path);
			}
		}
	}
	
}
