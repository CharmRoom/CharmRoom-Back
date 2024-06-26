package com.charmroom.charmroom.service;

import org.springframework.stereotype.Service;

import com.charmroom.charmroom.dto.business.AttachmentDto;
import com.charmroom.charmroom.dto.business.AttachmentMapper;
import com.charmroom.charmroom.entity.Attachment;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.repository.AttachmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttachmentService {
private final AttachmentRepository attachmentRepository;
	public AttachmentDto loadById(Integer id) {
		Attachment attachment = attachmentRepository.findById(id)
				.orElseThrow(() ->new BusinessLogicException(BusinessLogicError.NOTFOUND_ATTACHMENT, "id: " + id));
		return AttachmentMapper.toDto(attachment);
	}
}
