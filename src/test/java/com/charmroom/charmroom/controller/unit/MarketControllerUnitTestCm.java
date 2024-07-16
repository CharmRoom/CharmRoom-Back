package com.charmroom.charmroom.controller.unit;

import com.charmroom.charmroom.controller.api.MarketController;
import com.charmroom.charmroom.dto.business.MarketDto;
import com.charmroom.charmroom.dto.business.ArticleDto;
import com.charmroom.charmroom.dto.business.UserDto;
import com.charmroom.charmroom.dto.business.WishDto;
import com.charmroom.charmroom.entity.enums.MarketArticleState;
import com.charmroom.charmroom.entity.enums.UserLevel;
import com.charmroom.charmroom.exception.ExceptionHandlerAdvice;
import com.charmroom.charmroom.dto.presentation.ArticleDto.ArticleUpdateRequestDto;
import com.charmroom.charmroom.service.ArticleService;
import com.charmroom.charmroom.dto.business.ArticleDto;
import com.charmroom.charmroom.service.MarketService;
import com.charmroom.charmroom.dto.presentation.MarketDto.MarketUpdateRequestDto;
import com.charmroom.charmroom.service.WishService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class MarketControllerUnitTestCm {
    @Mock
    MarketService marketService;
    @Mock
    ArticleService articleService;
    @Mock
    WishService wishService;

    @InjectMocks
    MarketController marketController;

    MockMvc mockMvc;
    MarketDto mockedMarketDto;
    UserDto userDto;
    ArticleDto article;
    Gson gson;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(marketController)
                .setControllerAdvice(new ExceptionHandlerAdvice())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setValidator(mock(Validator.class))
                .build();

        userDto = UserDto.builder()
                .id(1)
                .username("")
                .level(UserLevel.ROLE_BASIC)
                .build();

        article = ArticleDto.builder()
                .user(userDto)
                .title("test")
                .body("test")
                .build();

        mockedMarketDto = MarketDto.builder()
                .id(1)
                .article(article)
                .price(10000)
                .tag("")
                .state(MarketArticleState.SALE)
                .build();

        gson = new Gson();
    }

    @Nested
    class Create {
        @Test
        void success_withEmptyFileList() throws Exception {
            // given
            doReturn(mockedMarketDto).when(marketService).create(any(MarketDto.class), any(), eq(1));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/market/1")
                    .param("article.title", mockedMarketDto.getArticle().getTitle())
                    .param("article.body", mockedMarketDto.getArticle().getBody())
                    .param("tag", mockedMarketDto.getTag())
                    .param("state", mockedMarketDto.getState().toString())
                    .param("price", mockedMarketDto.getPrice().toString())
            );

            // then
            resultActions.andExpectAll(
                    status().isCreated(),
                    jsonPath("$.data.article.title").value(mockedMarketDto.getArticle().getTitle()),
                    jsonPath("$.data.state").value(mockedMarketDto.getState().toString())
            );
        }

        @Test
        void success_withFileList() throws Exception {
            // given
            MockMultipartFile file = new MockMultipartFile("article.file", "test.png", "image/png", "test".getBytes());

            doReturn(mockedMarketDto).when(marketService).create(any(MarketDto.class), any(), eq(1), any());

            // when
            ResultActions resultActions = mockMvc.perform(multipart("/api/market/1")
                    .file(file).file(file).file(file)
                    .param("article.title", mockedMarketDto.getArticle().getTitle())
                    .param("article.body", mockedMarketDto.getArticle().getBody())
                    .param("tag", mockedMarketDto.getTag())
                    .param("state", mockedMarketDto.getState().toString())
                    .param("price", mockedMarketDto.getPrice().toString())
            );

            // then
            resultActions.andExpectAll(
                    status().isCreated(),
                    jsonPath("$.data.article.title").value(mockedMarketDto.getArticle().getTitle())
            );
        }
    }

    @Nested
    class GetMarket {
        @Test
        void success() throws Exception {
            // given
            doReturn(mockedMarketDto).when(marketService).getMarket(eq(1));

            // when
            ResultActions resultActions = mockMvc.perform(get("/api/market/1"));

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.code").value("OK")
            );
        }
    }

    @Nested
    class GetMarketList {
        @Test
        void success() throws Exception {
            // given
            List<MarketDto> dtoList = List.of(mockedMarketDto, mockedMarketDto, mockedMarketDto);

            PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("id").descending());
            PageImpl<MarketDto> dtoPage = new PageImpl<>(dtoList, pageRequest, 3);

            doReturn(dtoPage).when(marketService).getMarkets(1, pageRequest);

            // when
            ResultActions resultActions = mockMvc.perform(get("/api/market/list/1"));

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.content").isArray(),
                    jsonPath("$.data.content.size()").value(3),
                    jsonPath("$.data.content[0].article.body").value(mockedMarketDto.getArticle().getBody())
            );
        }
    }

    @Nested
    class Update {
        @Test
        void success() throws Exception {
            // given
            doReturn(mockedMarketDto).when(marketService).update(eq(1), any(MarketDto.class));

            ArticleUpdateRequestDto article = ArticleUpdateRequestDto.builder()
                    .title(mockedMarketDto.getArticle().getTitle())
                    .body(mockedMarketDto.getArticle().getBody())
                    .build();

            MarketUpdateRequestDto request = MarketUpdateRequestDto.builder()
                    .article(article)
                    .price(mockedMarketDto.getPrice())
                    .tag(mockedMarketDto.getTag())
                    .state(mockedMarketDto.getState())
                    .build();

            // when
            ResultActions resultActions = mockMvc.perform(patch("/api/market/1")
                    .content(gson.toJson(request))
                    .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.article.title").value(mockedMarketDto.getArticle().getTitle()),
                    jsonPath("$.data.article.body").value(mockedMarketDto.getArticle().getBody())
            );
        }
    }

    @Nested
    class Delete {
        @Test
        void success() throws Exception{
            // given
            doNothing().when(marketService).delete(eq(1));

            // when
            ResultActions resultActions = mockMvc.perform(delete("/api/market/1"));

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.data").doesNotExist()
            );
        }
    }

    @Nested
    class Wish {
        @Test
        void success() throws Exception {
            // given
            WishDto mockedWishDto = WishDto.builder()
                    .id(1)
                    .user(mockedMarketDto.getArticle().getUser())
                    .market(mockedMarketDto)
                    .build();

            doReturn(mockedWishDto).when(wishService).wishOrCancel(any(), eq(1));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/market/1/wish"));

            // then
            resultActions.andExpectAll(
                    status().isCreated(),
                    jsonPath("$.code").value("CREATED")
            );
        }
    }
}
