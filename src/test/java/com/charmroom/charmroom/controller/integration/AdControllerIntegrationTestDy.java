package com.charmroom.charmroom.controller.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.charmroom.charmroom.controller.integration.IntegrationTestBase.WithCharmroomUserDetails;
import com.charmroom.charmroom.entity.Ad;
import com.charmroom.charmroom.entity.Image;
import com.charmroom.charmroom.repository.AdRepository;
import com.charmroom.charmroom.repository.ImageRepository;

public class AdControllerIntegrationTestDy extends IntegrationTestBase {
	@Autowired
	ImageRepository imageRepository;
	@Autowired
	AdRepository adRepository;
	
	Ad buildAd(String s) {
		Image image = imageRepository.save(Image.builder()
				.path(s)
				.originalName(s)
				.build());
		
		return adRepository.save(Ad.builder()
				.title(s)
				.link(s)
				.start(LocalDateTime.of(2024, 8, 28, 0, 0))
				.end(LocalDateTime.of(2024, 8, 30, 0, 0))
				.image(image)
				.build());
	}

	@Nested
	class GetAllAdsActive {
		@Test
		void success() throws Exception {
			// given
			for(int i = 0; i < 3; i++) {
				buildAd(Integer.toString(i));
			}
			
			// when
			mockMvc.perform(get("/api/ad/all"))
			
			// then
			.andExpectAll(status().isOk(),
					jsonPath("$.data").isArray(),
					jsonPath("$.data.size()").value(3));
		}
	}
	
	

}
