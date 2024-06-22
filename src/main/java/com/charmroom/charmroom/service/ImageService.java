package com.charmroom.charmroom.service;

import org.springframework.stereotype.Service;

import com.charmroom.charmroom.entity.Image;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.repository.ImageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageService {
	private final ImageRepository imageRepository;
	
	public Image loadById(Integer id) {
		return imageRepository.findById(id).orElseThrow(() ->new BusinessLogicException(BusinessLogicError.NOTFOUND_IMAGE, "id: " + id));
	}
}
