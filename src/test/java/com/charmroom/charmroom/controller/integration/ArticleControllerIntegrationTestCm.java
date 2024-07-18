package com.charmroom.charmroom.controller.integration;

import com.charmroom.charmroom.controller.integration.IntegrationTestBase.WithCharmroomUserDetails;
import com.charmroom.charmroom.dto.presentation.ArticleDto;
import com.charmroom.charmroom.dto.presentation.ArticleDto.ArticleCreateRequestDto;
import com.charmroom.charmroom.dto.presentation.ArticleDto.ArticleUpdateRequestDto;
import com.charmroom.charmroom.entity.Article;
import com.charmroom.charmroom.entity.ArticleLike;
import com.charmroom.charmroom.entity.Board;
import com.charmroom.charmroom.entity.enums.BoardType;
import com.charmroom.charmroom.repository.ArticleLikeRepository;
import com.charmroom.charmroom.repository.ArticleRepository;
import com.charmroom.charmroom.repository.BoardRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithCharmroomUserDetails
public class ArticleControllerIntegrationTestCm extends IntegrationTestBase {
    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    BoardRepository boardRepository;

    String urlPrefix = "/api/article/";

    Board board = Board.builder()
            .name("board")
            .type(BoardType.LIST)
            .build();

    Article buildArticle(String title) {
        return Article.builder()
                .user(charmroomUser)
                .board(board)
                .title(title)
                .body("")
                .build();
    }

    @Nested
    class CreateArticle {
        ArticleCreateRequestDto dto = ArticleCreateRequestDto.builder()
                .title("test")
                .body("test")
                .build();

        @Test
        void success() throws Exception {
            // given
            Board saved = boardRepository.save(board);

            // when
            ResultActions resultActions = mockMvc.perform(multipart(urlPrefix + saved.getId())
                    .param("title", dto.getTitle())
                    .param("body", dto.getBody())
            );

            // then
            resultActions.andDo(print());
            resultActions.andExpectAll(
                    status().isCreated(),
                    jsonPath("$.data.title").value(dto.getTitle())
            );
        }

        @Test
        void fail_notFoundBoard() throws Exception {
            // given
            // when
            ResultActions resultActions = mockMvc.perform(multipart(urlPrefix + "1")
                    .param("title", dto.getTitle())
                    .param("body", dto.getBody()));
            // then
            resultActions.andExpectAll(
                    status().isNotFound(),
                    jsonPath("$.code").value("04100")
            );
        }
    }

    @Nested
    class GetArticle {

        @Test
        void success() throws Exception {
            // given
            boardRepository.save(board);
            Article article = articleRepository.save(buildArticle("test"));

            // when
            ResultActions resultActions = mockMvc.perform(get(urlPrefix + article.getId()));

            // then
            resultActions.andDo(print());
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.title").value(article.getTitle())
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
    class GetArticleList {
        @Test
        void success() throws Exception {
            // given
            boardRepository.save(board);
            List<Article> articles = new ArrayList<>();

            for (int i = 1; i <= 3; i++) {
                articles.add(articleRepository.save(buildArticle("test" + i)));
            }

            // when
            ResultActions resultActions = mockMvc.perform(get(urlPrefix + board.getId() + "/articles"));

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.content.size()").value(articles.size()),
                    jsonPath("$.data.content").isArray(),
                    jsonPath("$.data.content[0].title").value("test3")
            );
        }
    }

    @Nested
    class UpdateArticle {
        ArticleUpdateRequestDto dto = ArticleUpdateRequestDto.builder()
                .title("updated")
                .body("updated")
                .build();

        @Test
        void success() throws Exception {
            // given
            boardRepository.save(board);
            Article article = articleRepository.save(buildArticle("test"));

            // when
            ResultActions resultActions = mockMvc.perform(patch(urlPrefix + article.getId())
                    .content(gson.toJson(dto))
                    .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.title").value("updated")
            );
        }

        @Test
        void fail_notFoundArticle() throws Exception {
            // given
            // when
            ResultActions resultActions = mockMvc.perform(patch(urlPrefix + "1")
                    .content(gson.toJson(dto))
                    .contentType(MediaType.APPLICATION_JSON));
            // then
            resultActions.andExpectAll(
                    status().isNotFound(),
                    jsonPath("$.code").value("01100")
            );
        }
    }

    @Nested
    class Delete {
        @Test
        void success() throws Exception {
            // given
            boardRepository.save(board);
            Article article = articleRepository.save(buildArticle("test"));
            
            // when
            ResultActions resultActions = mockMvc.perform(delete(urlPrefix + article.getId()));

            // then
            resultActions.andExpectAll(
                    status().isOk()
            );
        }
    }

    @Nested
    class Like {
        @Autowired
        ArticleLikeRepository articleLikeRepository;

        @Test
        void success() throws Exception {
            // given
            boardRepository.save(board);
            Article article = articleRepository.save(buildArticle("test"));

            // when
            ResultActions resultActions = mockMvc.perform(post(urlPrefix + "like/" + article.getId()));

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.type").value(true)
            );
        }

        @Test
        void success_whenAlreadyLikeExists() throws Exception {
            // given
            boardRepository.save(board);
            Article article = articleRepository.save(buildArticle("test"));
            articleLikeRepository.save(ArticleLike.builder()
                    .user(charmroomUser)
                    .article(article)
                    .type(true)
                    .build());

            // when
            ResultActions resultActions = mockMvc.perform(post(urlPrefix + "like/" + article.getId()));

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.data").doesNotExist()
            );
        }

        @Test
        void success_whenAlreadyDislikeExists() throws Exception {
            // given
            boardRepository.save(board);
            Article article = articleRepository.save(buildArticle("test"));
            articleLikeRepository.save(ArticleLike.builder()
                    .user(charmroomUser)
                    .article(article)
                    .type(false)
                    .build());

            // when
            ResultActions resultActions = mockMvc.perform(post(urlPrefix + "like/" + article.getId()));

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.type").value(true)
            );
        }
    }

    @Nested
    class Dislike {
        @Autowired
        ArticleLikeRepository articleLikeRepository;

        @Test
        void success() throws Exception {
            // given
            boardRepository.save(board);
            Article article = articleRepository.save(buildArticle("test"));

            // when
            ResultActions resultActions = mockMvc.perform(post(urlPrefix + "dislike/" + article.getId()));

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.type").value(false)
            );
        }

        @Test
        void success_whenAlreadyDislikeExists() throws Exception {
            // given
            boardRepository.save(board);
            Article article = articleRepository.save(buildArticle("test"));
            articleLikeRepository.save(ArticleLike.builder()
                    .user(charmroomUser)
                    .article(article)
                    .type(false)
                    .build());

            // when
            ResultActions resultActions = mockMvc.perform(post(urlPrefix + "dislike/" + article.getId()));

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.data").doesNotExist()
            );
        }

        @Test
        void success_whenAlreadyLikeExists() throws Exception {
            // given
            boardRepository.save(board);
            Article article = articleRepository.save(buildArticle("test"));
            articleLikeRepository.save(ArticleLike.builder()
                    .user(charmroomUser)
                    .article(article)
                    .type(true)
                    .build());

            // when
            ResultActions resultActions = mockMvc.perform(post(urlPrefix + "dislike/" + article.getId()));

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.type").value(false)
            );
        }
    }
}
