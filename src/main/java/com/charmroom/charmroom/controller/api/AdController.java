package com.charmroom.charmroom.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.charmroom.charmroom.dto.business.AdMapper;
import com.charmroom.charmroom.dto.presentation.CommonResponseDto;
import com.charmroom.charmroom.service.AdService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ad")
@RequiredArgsConstructor
public class AdController {
	private final AdService adService;
	@GetMapping("/all")
	public ResponseEntity<?> getAllActiveAds(){
		var dtos = adService.getAllAdsActive();
		var response = dtos.stream().map(AdMapper::toResponse).toList();
		return CommonResponseDto.ok(response).toResponseEntity();
	}
}
