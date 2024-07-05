package com.charmroom.charmroom.controller.unit;

import com.charmroom.charmroom.controller.api.ArticleController;
import com.charmroom.charmroom.dto.business.ArticleDto;
import com.charmroom.charmroom.exception.ExceptionHandlerAdvice;
import com.charmroom.charmroom.service.ArticleService;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ArticleControllerUnitTestCm {
    @Mock
    ArticleService articleService;

    @InjectMocks
    ArticleController articleController;

    MockMvc mockMvc;
    ArticleDto mockedArticleDto;
    Gson gson;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(articleController)
                .setControllerAdvice(new ExceptionHandlerAdvice())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setValidator(mock(Validator.class))
                .build();

        mockedArticleDto = ArticleDto.builder()
                .id(1)
                .title("test")
                .body("test")
                .build();

        gson = new Gson();
    }

    @Nested
    class Create {
        @Test
        void success_withEmptyFileList() throws Exception {
            // given
            doReturn(mockedArticleDto).when(articleService).createArticle(any(), eq(1), eq(mockedArticleDto.getTitle()), eq(mockedArticleDto.getBody()));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/article/1")
                    .param("title", mockedArticleDto.getTitle())
                    .param("body", mockedArticleDto.getBody())
            );

            // then
            resultActions.andExpect(jsonPath("$.code").value("CREATED"))
                    .andExpect(status().isCreated())
                    .andDo(print());
        }

        @Test
        void success_withFileList() throws Exception{
            // given
            MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "test".getBytes());

            doReturn(mockedArticleDto).when(articleService).createArticle(any(), eq(1), eq(mockedArticleDto.getTitle()), eq(mockedArticleDto.getBody()), any());

            // when
            ResultActions resultActions = mockMvc.perform(multipart("/api/article/1")
                    .file(file).file(file).file(file)
                    .param("title", mockedArticleDto.getTitle())
                    .param("body", mockedArticleDto.getBody())
            );

            // then
            resultActions.andExpect(jsonPath("$.code").value("CREATED"))
                    .andExpect(status().isCreated())
                    .andDo(print());
        }
    }
}
