package com.charmroom.charmroom.controller.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.charmroom.charmroom.controller.integration.IntegrationTestBase.WithCharmroomUserDetails;
import com.charmroom.charmroom.dto.presentation.ArticleDto.ArticleCreateRequestDto;
import com.charmroom.charmroom.dto.presentation.ArticleDto.ArticleUpdateRequestDto;
import com.charmroom.charmroom.dto.presentation.MarketDto.MarketCreateRequestDto;
import com.charmroom.charmroom.dto.presentation.MarketDto.MarketUpdateRequestDto;
import com.charmroom.charmroom.entity.Article;
import com.charmroom.charmroom.entity.Board;
import com.charmroom.charmroom.entity.Market;
import com.charmroom.charmroom.entity.Wish;
import com.charmroom.charmroom.entity.enums.BoardType;
import com.charmroom.charmroom.entity.enums.MarketArticleState;
import com.charmroom.charmroom.repository.ArticleRepository;
import com.charmroom.charmroom.repository.BoardRepository;
import com.charmroom.charmroom.repository.MarketRepository;
import com.charmroom.charmroom.repository.WishRepository;

@WithCharmroomUserDetails
public class MarketControllerIntegrationTestCm extends IntegrationTestBase {
    @Autowired
    MarketRepository marketRepository;
    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    BoardRepository boardRepository;

    String urlPrefix = "/api/market/";

    Board board = Board.builder()
            .name("board")
            .type(BoardType.LIST)
            .build();

    Article article = Article.builder()
            .user(charmroomUser)
            .board(board)
            .title("test")
            .body("test")
            .build();

    Market buildMarket(String title) {
        return Market.builder()
                .article(article)
                .tag("test")
                .state(MarketArticleState.SALE)
                .price(1000)
                .build();
    }

    @Nested
    class Create{
        ArticleCreateRequestDto articleDto = ArticleCreateRequestDto.builder()
                .title("test")
                .body("test")
                .build();

        MarketCreateRequestDto dto = MarketCreateRequestDto.builder()
                .article(articleDto)
                .price(1000)
                .state(MarketArticleState.SALE)
                .tag("test")
                .build();

        @Test
        void success() throws Exception {
            // given
            Board savedBoard = boardRepository.save(board);

            // when
            ResultActions resultActions = mockMvc.perform(multipart(urlPrefix + savedBoard.getId())
                    .param("article.title", dto.getArticle().getTitle())
                    .param("article.body", dto.getArticle().getBody())
                    .param("tag", dto.getTag())
                    .param("state", dto.getState().toString())
                    .param("price", dto.getPrice().toString())
            );

            // then
            resultActions.andExpectAll(
                    status().isCreated(),
                    jsonPath("$.data.article.title").value(dto.getArticle().getTitle()),
                    jsonPath("$.data.tag").value(dto.getTag())
            );
        }

        @Test
        void fail_notFoundBoard() throws Exception {
            // given
            // when
            ResultActions resultActions = mockMvc.perform(multipart(urlPrefix + "1")
                    .param("article.title", dto.getArticle().getTitle())
                    .param("article.body", dto.getArticle().getBody())
                    .param("tag", dto.getTag())
                    .param("state", dto.getState().toString())
                    .param("price", dto.getPrice().toString()));

            // then
            resultActions.andExpectAll(
                    status().isNotFound(),
                    jsonPath("$.code").value("04100")
            );
        }
    }

    @Nested
    class GetMarket {
        @Test
        void success() throws Exception {
            // given
            boardRepository.save(board);
            articleRepository.save(article);
            Market market = marketRepository.save(buildMarket("test"));

            // when
            ResultActions resultActions = mockMvc.perform(get(urlPrefix + market.getId()));

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.tag").value(market.getTag())
            );
        }

        @Test
        void fail_notFoundArticle() throws Exception {
            // given
            // when
            ResultActions resultActions = mockMvc.perform(get(urlPrefix + "1"));
            // then
            resultActions.andExpectAll(
                    status().isNotFound(),
                    jsonPath("$.code").value("01100")
            );
        }
    }

    @Nested
    class GetMarketList {
        @Test
        void success() throws Exception {
            // given
            Board savedBoard = boardRepository.save(board);

            List<Article> articles = new ArrayList<>();
            for (int i = 1; i <= 3; i++) {
                articles.add(articleRepository.save(Article.builder()
                        .board(board)
                        .title("test" + i)
                        .body("test" + i)
                        .build()));
            }

            for (int i = 0; i < 3; i++) {
                marketRepository.save(Market.builder()
                        .article(articles.get(i))
                        .tag("")
                        .price(1000)
                        .state(MarketArticleState.SALE)
                        .build());
            }

            // when
            ResultActions resultActions = mockMvc.perform(get(urlPrefix + "list/" + savedBoard.getId()));

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.content.size()").value(3)
            );
        }
    }

    @Nested
    class Update {
        @Test
        void success() throws Exception {
            // given
            Board board = boardRepository.save(Board.builder()
                    .name("board")
                    .type(BoardType.LIST)
                    .build());

            Article article = articleRepository.save(Article.builder()
                    .user(charmroomUser)
                    .board(board)
                    .title("test")
                    .body("test")
                    .build());

            Market market = marketRepository.save(Market.builder()
                    .article(article)
                    .tag("test")
                    .state(MarketArticleState.SALE)
                    .price(1000)
                    .build());

            ArticleUpdateRequestDto articleDto = ArticleUpdateRequestDto.builder()
                    .title("updated")
                    .body("updated")
                    .build();

            MarketUpdateRequestDto dto = MarketUpdateRequestDto.builder()
                    .article(articleDto)
                    .price(10000)
                    .tag("")
                    .state(MarketArticleState.SALE)
                    .build();

            // when
            ResultActions resultActions = mockMvc.perform(patch(urlPrefix + market.getId())
                    .content(gson.toJson(dto))
                    .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.article.title").value("updated")
            );
        }
    }

    @Nested
    class Delete {
        @Test
        void success() throws Exception {
            // given
            Board board = boardRepository.save(Board.builder()
                    .name("board")
                    .type(BoardType.LIST)
                    .build());

            Article article = articleRepository.save(Article.builder()
                    .user(charmroomUser)
                    .board(board)
                    .title("test")
                    .body("test")
                    .build());

            Market market = marketRepository.save(Market.builder()
                    .article(article)
                    .tag("test")
                    .state(MarketArticleState.SALE)
                    .price(1000)
                    .build());

            // when
            ResultActions resultActions = mockMvc.perform(delete(urlPrefix + market.getId()));

            // then
            resultActions.andExpectAll(
                    status().isOk()
            );
        }
    }

    @Nested
    class WishMarket {
        @Autowired
        WishRepository wishRepository;

        @Test
        void success() throws Exception {
            // given
            boardRepository.save(board);
            articleRepository.save(article);
            Market market = marketRepository.save(buildMarket("test"));

            // when
            ResultActions resultActions = mockMvc.perform(post(urlPrefix + market.getId() + "/wish"));

            // then
            resultActions.andExpectAll(
                    status().isCreated(),
                    jsonPath("$.data.market.tag").value(market.getTag())
            );
        }

        @Test
        void successWhenAlreadyWishExists() throws Exception {
            // given
            boardRepository.save(board);
            articleRepository.save(article);
            Market market = marketRepository.save(buildMarket("test"));

            wishRepository.save(Wish.builder()
                    .user(charmroomUser)
                    .market(market)
                    .build());

            // when
            ResultActions resultActions = mockMvc.perform(post(urlPrefix + market.getId() + "/wish"));

            // then
            resultActions.andExpectAll(
                    status().isCreated(),
                    jsonPath("$.data").doesNotExist()
            );
        }
    }
}
