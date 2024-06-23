package com.charmroom.charmroom.service;

import org.springframework.stereotype.Service;

import com.charmroom.charmroom.entity.Attachment;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.repository.AttachmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttachmentService {
private final AttachmentRepository attachmentRepository;
	public Attachment loadById(Integer id) {
		return attachmentRepository.findById(id).orElseThrow(() ->new BusinessLogicException(BusinessLogicError.NOTFOUND_ATTACHMENT, "id: " + id));
	}
}
