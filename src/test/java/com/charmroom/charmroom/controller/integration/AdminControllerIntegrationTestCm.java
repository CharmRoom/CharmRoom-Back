package com.charmroom.charmroom.controller.integration;

import com.charmroom.charmroom.controller.integration.IntegrationTestBase.WithCharmroomUserDetails;
import com.charmroom.charmroom.dto.presentation.AdDto.AdCreateRequestDto;
import com.charmroom.charmroom.dto.presentation.AdDto;
import com.charmroom.charmroom.entity.Ad;
import com.charmroom.charmroom.entity.Image;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.entity.enums.UserLevel;
import com.charmroom.charmroom.repository.AdRepository;
import com.charmroom.charmroom.repository.ImageRepository;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTestBase.WithCharmroomAdminDetails
public class AdminControllerIntegrationTestCm extends IntegrationTestBase {

    @Nested
    class CreateAd {
        MockHttpServletRequestBuilder request = multipart("/api/admin/ad");
        @Autowired
        ImageRepository imageRepository;

        @Test
        void success() throws Exception {
            // given
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

            MultipartFile file = new MockMultipartFile("image", "profile.png", MediaType.IMAGE_PNG_VALUE, "test".getBytes());

            AdCreateRequestDto dto = AdCreateRequestDto.builder()
                    .title("test title")
                    .start(LocalDateTime.of(2024, 1, 1, 1, 1))
                    .end(LocalDateTime.of(2024, 1, 1, 1, 2))
                    .link("")
                    .image(file)
                    .build();

            // when
            ResultActions resultActions = mockMvc.perform(request
                    .content(objectMapper.writeValueAsString(dto))
                    .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpectAll(
                    status().isCreated(),
                    jsonPath("$.data.title").value(dto.getTitle())
            );
        }
    }

//    @Nested
//    class GetAds {
//        MockHttpServletRequestBuilder request = get("/api/admin/ad");
//        @Autowired
//        AdRepository adRepository;
//        @Autowired
//        ImageRepository imageRepository;
//
//        Ad buildAd(String prefix) {
//            Image image = Image.builder()
//                    .originalName(prefix)
//                    .path(prefix)
//                    .build();
//            imageRepository.save(image);
//            return Ad.builder()
//                    .title(prefix)
//                    .start()
//                    .end()
//                    .link(prefix)
//                    .image(image)
//                    .build();
//        }
//
//        @Test
//        void success() throws Exception {
//            // given
//            for (int i = 1; i <= 3; i++) {
//                adRepository.save(buildAd("ad" + i));
//            }
//
//            // when
//            ResultActions resultActions = mockMvc.perform(request);
//
//            // then
//            resultActions.andExpectAll(
//                    status().isOk(),
//                    jsonPath("$.data.totalElements").value(adRepository.count()),
//                    jsonPath("$.data.content.size()").value(adRepository.count())
//            );
//        }
//    }
}
