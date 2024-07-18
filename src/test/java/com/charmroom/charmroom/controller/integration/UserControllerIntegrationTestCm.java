package com.charmroom.charmroom.controller.integration;

import com.charmroom.charmroom.dto.presentation.SubscribeDto;
import com.charmroom.charmroom.dto.presentation.SubscribeDto.SubscribeCreateRequestDto;
import com.charmroom.charmroom.entity.Article;
import com.charmroom.charmroom.entity.Subscribe;
import com.charmroom.charmroom.entity.Subscribe;
import com.charmroom.charmroom.entity.Market;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.entity.Wish;
import com.charmroom.charmroom.entity.enums.MarketArticleState;
import com.charmroom.charmroom.entity.enums.UserLevel;
import com.charmroom.charmroom.repository.ArticleRepository;
import com.charmroom.charmroom.repository.MarketRepository;
import com.charmroom.charmroom.repository.SubscribeRepository;
import com.charmroom.charmroom.repository.UserRepository;
import com.charmroom.charmroom.repository.WishRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerIntegrationTestCm extends IntegrationTestBase {
    @Nested
    class getMyArticles {
        MockHttpServletRequestBuilder request = get("/api/user/article");
        @Autowired
        ArticleRepository articleRepository;

        @Test
        @WithCharmroomUserDetails
        void success() throws Exception {
            // given

            List<Article> articles = new ArrayList<>();

            for (int i = 1; i <= 3; i++) {
                articles.add(
                        articleRepository.save(Article.builder()
                                .user(charmroomUser)
                                .title("title" + i)
                                .body("body" + i)
                                .build())
                );
            }

            // when
            ResultActions resultActions = mockMvc.perform(request);

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.totalElements").value(3),
                    jsonPath("$.data.content.size()").value(3),
                    jsonPath("$.data.content").isArray(),
                    jsonPath("$.data.content[0].title").value("title3")
            );
        }
    }

    @Nested
    class GetMyWishes {
        MockHttpServletRequestBuilder request = get("/api/user/wish");
        @Autowired
        WishRepository wishRepository;
        @Autowired
        ArticleRepository articleRepository;
        @Autowired
        MarketRepository marketRepository;

        @Test
        @WithCharmroomUserDetails
        void success() throws Exception {
            // given
            List<Market> markets = new ArrayList<>();
            for (int i = 1; i <= 3; i++) {
                Article saved = articleRepository.save(Article.builder()
                        .user(charmroomUser)
                        .title("title" + i)
                        .body("body" + i)
                        .build());

                markets.add(marketRepository.save(Market.builder()
                        .tag("")
                        .article(saved)
                        .state(MarketArticleState.SALE)
                        .price(1000)
                        .build()));
            }

            List<Wish> wishes = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                wishes.add(wishRepository.save(Wish.builder()
                        .market(markets.get(i))
                        .user(charmroomUser)
                        .build()));
            }

            // when
            ResultActions resultActions = mockMvc.perform(request);

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.totalElements").value(3),
                    jsonPath("$.data.content.size()").value(3),
                    jsonPath("$.data.content").isArray()
            );
        }
    }

    @Nested
    class CreateSubscribe {
        MockHttpServletRequestBuilder request = post("/api/user");
        @Autowired
        SubscribeRepository subscribeRepository;
        @Autowired
        UserRepository userRepository;

        @Test
        @WithCharmroomUserDetails
        void success() throws Exception {
            // given
            User target = userRepository.save(User.builder()
                    .username("target")
                    .password("")
                    .email("")
                    .nickname("")
                    .level(UserLevel.ROLE_BASIC)
                    .build());

            SubscribeCreateRequestDto dto = SubscribeCreateRequestDto.builder()
                    .targetUserName(target.getUsername())
                    .subscriberUserName(charmroomUser.getUsername())
                    .build();

            // when
            ResultActions resultActions = mockMvc.perform(request
                    .content(gson.toJson(dto))
                    .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andDo(print());
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.target.username").value(dto.getTargetUserName())
            );
        }
    }

    @Nested
    class GetMySubscribes {
        MockHttpServletRequestBuilder request = get("/api/user/subscribe");
        @Autowired
        SubscribeRepository subscribeRepository;
        @Autowired
        UserRepository userRepository;

        @Test
        @WithCharmroomUserDetails
        void success() throws Exception {
            // given
            List<User> targets = new ArrayList<>();
            for (int i = 1; i <= 3; i++) {
                targets.add(userRepository.save(User.builder()
                        .username("target" + i)
                        .password("" + i)
                        .email("" + i)
                        .nickname("" + i)
                        .level(UserLevel.ROLE_BASIC)
                        .build()
                ));
            }

            for (int i = 0; i < 3; i++) {
                subscribeRepository.save(Subscribe.builder()
                        .subscriber(charmroomUser)
                        .target(targets.get(i))
                        .build());
            }

            // when
            ResultActions resultActions = mockMvc.perform(request);

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.totalElements").value(3),
                    jsonPath("$.data.content.size()").value(3),
                    jsonPath("$.data.content").isArray());
        }
    }
}
