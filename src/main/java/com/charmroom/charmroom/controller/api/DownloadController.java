package com.charmroom.charmroom.controller.api;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.charmroom.charmroom.dto.business.AttachmentDto;
import com.charmroom.charmroom.dto.business.ImageDto;
import com.charmroom.charmroom.service.AttachmentService;
import com.charmroom.charmroom.service.ImageService;
import com.charmroom.charmroom.util.CharmroomUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/download")
@RequiredArgsConstructor
public class DownloadController {
	private final ImageService imageService;
	private final AttachmentService attachmentService;
	private final CharmroomUtil.Upload uploadUtil;
	
	@GetMapping("/image/{imageId}")
	public ResponseEntity<Resource> downloadImage(
			@PathVariable("imageId") final Integer imageId
			) {
		ImageDto image = imageService.loadById(imageId);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION,
						"attachment; filename=\"" + image.getOriginalName() + "\";")
				.body(image.getResource())
				;
	}
	
	@GetMapping("/attachment/{attachmentId}")
	public ResponseEntity<Resource> downloadAttachment(
			@PathVariable("attachmentId") final Integer attachmentId
			){
		AttachmentDto attachment = attachmentService.loadById(attachmentId);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION,
						"attachment; filename=\"" + attachment.getOriginalName() + "\";")
				.body(attachment.getResource())
				;
	}
}
