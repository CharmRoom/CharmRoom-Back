package com.charmroom.charmroom.controller.unit;

import com.charmroom.charmroom.controller.api.ArticleController;
import com.charmroom.charmroom.dto.business.ArticleDto;
import com.charmroom.charmroom.dto.business.ArticleLikeDto;
import com.charmroom.charmroom.dto.presentation.ArticleDto.ArticleUpdateRequestDto;
import com.charmroom.charmroom.exception.ExceptionHandlerAdvice;
import com.charmroom.charmroom.service.ArticleLikeService;
import com.charmroom.charmroom.service.ArticleService;
import com.google.gson.Gson;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ArticleControllerUnitTestCm {
    @Mock
    ArticleService articleService;
    @Mock
    ArticleLikeService articleLikeService;

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

    @Nested
    class GetArticle {
        @Test
        void success() throws Exception {
            // given
            doReturn(mockedArticleDto).when(articleService).getOneArticle(1);

            // when
            ResultActions resultActions = mockMvc.perform(get("/api/article/1"));

            // then
            resultActions.andExpect(jsonPath("$.code").value("OK"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class GetArticleList {
        @Test
        void success() throws Exception {
            // given
            List<ArticleDto> dtoList = List.of(mockedArticleDto, mockedArticleDto, mockedArticleDto);

            PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("id").descending());
            PageImpl<ArticleDto> dtoPage = new PageImpl<>(dtoList, pageRequest, 3);

            doReturn(dtoPage).when(articleService).getArticles(1, pageRequest);

            // when
            ResultActions resultActions = mockMvc.perform(get("/api/article/1/articles"));

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.content").isArray(),
                    jsonPath("$.data.content.size()").value(3),
                    jsonPath("$.data.content[0].body").value("test")
            );
        }
    }

    @Nested
    class Update {
        @Test
        void success() throws Exception {
            // given
            doReturn(mockedArticleDto).when(articleService).updateArticle(eq(1), any(), eq(mockedArticleDto.getTitle()), eq(mockedArticleDto.getBody()));

            ArticleUpdateRequestDto request = ArticleUpdateRequestDto.builder()
                    .title(mockedArticleDto.getTitle())
                    .body(mockedArticleDto.getBody())
                    .build();

            // when
            ResultActions resultActions = mockMvc.perform(patch("/api/article/1")
                    .content(gson.toJson(request))
                    .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions.andExpectAll(
                    status().isOk()
                    , jsonPath("$.code").value("OK")
                    , jsonPath("$.data.title").value(mockedArticleDto.getTitle())
                    , jsonPath("$.data.body").value(mockedArticleDto.getBody())
            );

        }
    }

    @Nested
    class Delete {
        @Test
        void success() throws Exception {
            // given
            doNothing().when(articleService).deleteArticle(1);

            // when
            ResultActions resultActions = mockMvc.perform(delete("/api/article/1"));

            // then
            resultActions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").doesNotExist());
        }
    }

    @Nested
    class Like {
        @Test
        void success() throws Exception {
            // given
            ArticleLikeDto mockedArticleLikeDto = ArticleLikeDto.builder()
                    .type(true)
                    .build();

            doReturn(mockedArticleLikeDto).when(articleLikeService).like(any(), eq(1));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/article/like/1"));

            // then
            resultActions.andExpectAll(
                    status().isOk()
                    , jsonPath("$.data.type").value(true)
            );
        }
    }

    @Nested
    class Dislike {
        @Test
        void success() throws Exception {
            // given
            ArticleLikeDto mockedArticleLikeDto = ArticleLikeDto.builder()
                    .type(false)
                    .build();

            doReturn(mockedArticleLikeDto).when(articleLikeService).dislike(any(), eq(1));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/article/dislike/1"));

            // then
            resultActions.andExpectAll(
                    status().isOk()
                    , jsonPath("$.data.type").value(false)
            );
        }
    }
}
