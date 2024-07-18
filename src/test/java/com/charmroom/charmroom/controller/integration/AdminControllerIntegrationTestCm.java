package com.charmroom.charmroom.controller.integration;

import com.charmroom.charmroom.controller.integration.IntegrationTestBase.WithCharmroomUserDetails;
import com.charmroom.charmroom.dto.presentation.AdDto.AdCreateRequestDto;
import com.charmroom.charmroom.dto.presentation.AdDto;
import com.charmroom.charmroom.dto.presentation.AdDto.AdUpdateRequestDto;
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
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTestBase.WithCharmroomAdminDetails
public class AdminControllerIntegrationTestCm extends IntegrationTestBase {

    @Nested
    class CreateAd {
        MockMultipartHttpServletRequestBuilder request = multipart("/api/admin/ad");
        @Autowired
        ImageRepository imageRepository;

        @Test
        void success() throws Exception {
            // given
            MockMultipartFile file = new MockMultipartFile("image", "profile.png", MediaType.IMAGE_PNG_VALUE, "test".getBytes());

            // when
            ResultActions resultActions = mockMvc.perform(request
                    .file(file)
                    .param("title", "title")
                    .param("link", "link")
                    .param("start", "2024-03-03T00:00:00")
                    .param("end", "2024-03-04T00:00:00")
            );

            // then
            resultActions.andExpectAll(
                    status().isCreated(),
                    jsonPath("$.data.title").value("title")
            );
        }
    }

    @Nested
    class GetAds {
        MockHttpServletRequestBuilder request = get("/api/admin/ad");
        @Autowired
        AdRepository adRepository;
        @Autowired
        ImageRepository imageRepository;

        Ad buildAd(String prefix) {
            Image image = Image.builder()
                    .originalName(prefix)
                    .path(prefix)
                    .build();
            imageRepository.save(image);

            return Ad.builder()
                    .title(prefix)
                    .start(LocalDateTime.of(2024, 1, 1, 1, 1, 1))
                    .end(LocalDateTime.of(2024, 12, 1, 1, 1, 1))
                    .link(prefix)
                    .image(image)
                    .build();
        }

        @Test
        void success() throws Exception {
            // given
            for (int i = 1; i <= 3; i++) {
                adRepository.save(buildAd("ad" + i));
            }

            // when
            ResultActions resultActions = mockMvc.perform(request);

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.totalElements").value(adRepository.count()),
                    jsonPath("$.data.content.size()").value(adRepository.count())
            );
        }
    }

    @Nested
    class Update {
        @Autowired
        AdRepository adRepository;
        @Autowired
        ImageRepository imageRepository;

        MockMultipartFile file = new MockMultipartFile("image", "profile.png", MediaType.IMAGE_PNG_VALUE, "test".getBytes());

        @Test
        void success() throws Exception {
            // given
            Image image = imageRepository.save(Image.builder()
                    .originalName("")
                    .path("")
                    .build());

            Ad ad = adRepository.save(Ad.builder()
                    .title("test")
                    .link("test")
                    .start(LocalDateTime.of(2024, 1, 1, 1, 1, 1))
                    .end(LocalDateTime.of(2024, 12, 1, 1, 1, 1))
                    .image(image)
                    .build());

            // when
            ResultActions resultActions = mockMvc.perform(multipart("/api/admin/ad/" + ad.getId())
                    .file(file)
                    .param("title", "updated")
                    .param("link", "link")
                    .param("start", "2024-03-03T00:00:00")
                    .param("end", "2024-03-04T00:00:00")

            );

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.title").value("updated")
            );
        }
    }

    @Nested
    class Delete {
        @Autowired
        AdRepository adRepository;

        @Test
        void success() throws Exception {
            // given
            Ad ad = adRepository.save(Ad.builder()
                    .title("test")
                    .link("test")
                    .start(LocalDateTime.of(2024, 1, 1, 1, 1, 1))
                    .end(LocalDateTime.of(2024, 12, 1, 1, 1, 1))
                    .build());

            // when
            ResultActions resultActions = mockMvc.perform(delete("/api/admin/ad/" + ad.getId()));

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.code").value("OK")
            );
        }
    }
}
