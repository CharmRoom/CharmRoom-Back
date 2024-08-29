package com.charmroom.charmroom.controller.unit;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import com.charmroom.charmroom.controller.api.AdController;
import com.charmroom.charmroom.dto.business.AdDto;
import com.charmroom.charmroom.dto.business.AdMapper;
import com.charmroom.charmroom.entity.Ad;
import com.charmroom.charmroom.entity.Image;
import com.charmroom.charmroom.exception.ExceptionHandlerAdvice;
import com.charmroom.charmroom.service.AdService;
import com.google.gson.Gson;

@ExtendWith(MockitoExtension.class)
public class AdControllerUnitTestDy {
	@Mock
	AdService adService;
	
	@InjectMocks
	AdController adController;
	
	MockMvc mockMvc;
	
	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders
				.standaloneSetup(adController)
				.setControllerAdvice(new ExceptionHandlerAdvice())
				.build();
	}
	
	Ad buildAd(String s) {
		return Ad.builder()
				.title(s)
				.link(s)
				.start(LocalDateTime.of(2024, 8, 28, 0, 0))
				.end(LocalDateTime.of(2024, 8, 30, 0, 0))
				.image(Image.builder()
						.path(s)
						.originalName(s)
						.build())
				.build();
	}
	@Nested
	class GetAllAdsActive {
		@Test
		void success() throws Exception {
			// given
			List<AdDto> dtos = new ArrayList<>();
			for(int i = 0; i < 3; i++) {
				dtos.add(AdMapper.toDto(buildAd(Integer.toString(i))));
			}
			doReturn(dtos).when(adService).getAllAdsActive();
			// when
			mockMvc.perform(get("/api/ad/all"))
			// then
			.andExpectAll(status().isOk(),
					jsonPath("$.data").isArray(),
					jsonPath("$.data.size()").value(3));
		}
	}
	
	
	
}
