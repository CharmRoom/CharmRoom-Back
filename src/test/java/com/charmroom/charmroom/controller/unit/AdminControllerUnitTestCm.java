package com.charmroom.charmroom.controller.unit;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import com.charmroom.charmroom.controller.api.AdminController;
import com.charmroom.charmroom.dto.business.AdDto;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.exception.ExceptionHandlerAdvice;
import com.charmroom.charmroom.service.AdService;
import com.google.gson.Gson;

@ExtendWith(MockitoExtension.class)
public class AdminControllerUnitTestCm {
    @Mock
    AdService adService;

    @InjectMocks
    AdminController adminController;

    MockMvc mockMvc;
    AdDto mockedAdDto;

    Gson gson;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(adminController)
                .setControllerAdvice(new ExceptionHandlerAdvice())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setValidator(mock(Validator.class))
                .build();

        mockedAdDto = AdDto.builder()
                .id(1)
                .title("title")
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .link("")
                .build();

        gson = new Gson();
    }

    @Nested
    class CreateAdd {
        @Test
        void success() throws Exception {
            // given
            MockMultipartFile imageFile = new MockMultipartFile("image", "test.png", MediaType.IMAGE_PNG_VALUE, "test".getBytes());

            doReturn(mockedAdDto).when(adService).create(mockedAdDto.getTitle(), mockedAdDto.getLink(), mockedAdDto.getStart(), mockedAdDto.getEnd(), imageFile);

            // when
            ResultActions resultActions = mockMvc.perform(multipart("/api/admin/ad")
                    .file(imageFile)
                    .param("title", mockedAdDto.getTitle())
                    .param("link", mockedAdDto.getLink())
                    .param("start", mockedAdDto.getStart().toString())
                    .param("end", mockedAdDto.getEnd().toString())
            );

            // then
            resultActions.andExpectAll(
                    status().isCreated(),
                    jsonPath("$.code").value("CREATED"),
                    jsonPath("$.data.title").value(mockedAdDto.getTitle())
            );
        }
    }

    @Nested
    class GetArticles {
        @Test
        void success() throws Exception {
            // given
            List<AdDto> dtoList = List.of(mockedAdDto, mockedAdDto, mockedAdDto);

            PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("id").descending());
            PageImpl<AdDto> dtoPage = new PageImpl<>(dtoList, pageRequest, 3);

            doReturn(dtoPage).when(adService).getAllAdsByPageable(pageRequest);

            // when
            ResultActions resultActions = mockMvc.perform(get("/api/admin/ad"));

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.content.size()").value(3),
                    jsonPath("$.data.content[0].title").value(mockedAdDto.getTitle())
            );
        }
    }

    @Nested
    class Update {
        @Test
        void success() throws Exception {
            // given
            MockMultipartFile imageFile = new MockMultipartFile("image", "test.png", MediaType.IMAGE_PNG_VALUE, "test".getBytes());

            doReturn(mockedAdDto).when(adService).updateAd(mockedAdDto.getId(), mockedAdDto.getTitle(), mockedAdDto.getLink(), mockedAdDto.getStart(), mockedAdDto.getEnd(), imageFile);

            // when
            ResultActions resultActions = mockMvc.perform(multipart("/api/admin/ad/1")
                    .file(imageFile)
                    .param("title", mockedAdDto.getTitle())
                    .param("link", mockedAdDto.getLink())
                    .param("start", mockedAdDto.getStart().toString())
                    .param("end", mockedAdDto.getEnd().toString())
            );

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.title").value(mockedAdDto.getTitle())
            );
        }

        @Test
        void fail_NotFoundAd() throws Exception {
            // given
            MockMultipartFile imageFile = new MockMultipartFile("image", "test.png", MediaType.IMAGE_PNG_VALUE, "test".getBytes());

            doThrow(new BusinessLogicException(BusinessLogicError.NOTFOUND_AD))
                    .when(adService)
                    .updateAd(mockedAdDto.getId(), mockedAdDto.getTitle(), mockedAdDto.getLink(), mockedAdDto.getStart(), mockedAdDto.getEnd(), imageFile);

            // when
            ResultActions resultActions = mockMvc.perform(multipart("/api/admin/ad/1")
                    .file(imageFile)
                    .param("title", mockedAdDto.getTitle())
                    .param("link", mockedAdDto.getLink())
                    .param("start", mockedAdDto.getStart().toString())
                    .param("end", mockedAdDto.getEnd().toString()));

            // then
            resultActions.andExpectAll(
                    status().isNotFound(),
                    jsonPath("code").value("00100")
            );
        }
    }

    @Nested
    class DeleteAd {
        @Test
        void success() throws Exception {
            // given
            doNothing().when(adService).deleteAd(eq(1));

            // when
            ResultActions resultActions = mockMvc.perform(delete("/api/admin/ad/1"));

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.code").value("OK")
            );
        }
    }
}
