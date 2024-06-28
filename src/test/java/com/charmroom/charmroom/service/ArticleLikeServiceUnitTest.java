package com.charmroom.charmroom.service;

import com.charmroom.charmroom.dto.business.ArticleLikeDto;
import com.charmroom.charmroom.entity.Article;
import com.charmroom.charmroom.entity.ArticleLike;
import com.charmroom.charmroom.entity.Board;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.entity.enums.BoardType;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.repository.ArticleLikeRepository;
import com.charmroom.charmroom.repository.ArticleRepository;
import com.charmroom.charmroom.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ArticleLikeServiceUnitTest {
    @Mock
    private ArticleLikeRepository articleLikeRepository;
    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @InjectMocks
    private ArticleLikeService articleLikeService;

    private Article article;
    private User user;
    private ArticleLike articleLike;
    private ArticleLike articleDislike;
    private Board board;
    private String title;
    private String body;

    private User createUser() {
        return User.builder()
                .id(1)
                .username("username")
                .nickname("nickname")
                .email("email@example.com")
                .password(passwordEncoder.encode("password"))
                .build();
    }

    private Board createBoard() {
        return Board.builder()
                .type(BoardType.LIST)
                .exposed(false)
                .build();
    }

    private Article createArticle() {
        user = createUser();
        board = createBoard();

        return Article.builder()
                .id(1)
                .title(title)
                .body(body)
                .user(user)
                .board(board)
                .build();
    }

    @BeforeEach
    void setUp() {
        title = "test title";
        body = "test body";
        user = createUser();
        board = createBoard();
        article = createArticle();

        articleLike = ArticleLike.builder()
                .article(article)
                .type(true)
                .user(user)
                .build();

        articleDislike = ArticleLike.builder()
                .article(article)
                .user(user)
                .type(false)
                .build();
    }

    @Nested
    @DisplayName("Add Like")
    class addLike {
        @Test
        void success() {
            // given
            doReturn(Optional.of(user)).when(userRepository).findByUsername(user.getUsername());
            doReturn(Optional.of(article)).when(articleRepository).findById(article.getId());
            doReturn(articleLike).when(articleLikeRepository).save(any(ArticleLike.class));
            doReturn(Optional.empty()).when(articleLikeRepository).findByUserAndArticle(user, article);

            // when
            ArticleLikeDto like = articleLikeService.likeOrDislike(user.getUsername(), article.getId(), true);

            // then
            verify(articleLikeRepository).save(any(ArticleLike.class));
            assertThat(like).isNotNull();
        }

        @Test
        void AddLikeWhenOriginalTypeIsTrue() {
            // given
            doReturn(Optional.of(user)).when(userRepository).findByUsername(user.getUsername());
            doReturn(Optional.of(article)).when(articleRepository).findById(article.getId());

            when(articleLikeRepository.findByUserAndArticle(user, article)).thenReturn(Optional.of(articleLike));

            // when
            ArticleLikeDto like = articleLikeService.likeOrDislike(user.getUsername(), article.getId(), true);
            // then
            verify(articleLikeRepository).delete(any(ArticleLike.class));
            assertNull(like);
        }

        @Test
        void AddLikeWhenOriginalTypeIsFalse() {
            // given
            doReturn(Optional.of(user)).when(userRepository).findByUsername(user.getUsername());
            doReturn(Optional.of(article)).when(articleRepository).findById(article.getId());

            when(articleLikeRepository.findByUserAndArticle(user, article)).thenReturn(Optional.of(articleDislike));

            // when
            ArticleLikeDto like = articleLikeService.likeOrDislike(user.getUsername(), article.getId(), true);

            // then
            assertThat(like.getType()).isEqualTo(true);
        }

        @Test
        void fail_notExistingArticle() {
            // given
            doReturn(Optional.of(user)).when(userRepository).findByUsername(user.getUsername());
            doReturn(Optional.empty()).when(articleRepository).findById(article.getId());

            // when
            BusinessLogicException thrown = assertThrows(BusinessLogicException.class, () -> {
                articleLikeService.likeOrDislike(user.getUsername(), article.getId(), true);
            });

            // then
            assertThat(thrown.getError()).isEqualTo(BusinessLogicError.NOTFOUND_ARTICLE);
        }
    }

    @Nested
    @DisplayName("Add Dislike")
    class addDislike {
        @Test
        void success() {
            // given
            doReturn(Optional.of(user)).when(userRepository).findByUsername(user.getUsername());
            doReturn(Optional.of(article)).when(articleRepository).findById(article.getId());
            doReturn(articleDislike).when(articleLikeRepository).save(any(ArticleLike.class));
            doReturn(Optional.empty()).when(articleLikeRepository).findByUserAndArticle(user, article);

            // when
            ArticleLikeDto dislike = articleLikeService.likeOrDislike(user.getUsername(), article.getId(), false);

            // then
            verify(articleLikeRepository).save(any(ArticleLike.class));
            assertThat(dislike).isNotNull();
        }

        @Test
        void AddDislikeWhenOriginalTypeIsFalse() {
            // given
            doReturn(Optional.of(user)).when(userRepository).findByUsername(user.getUsername());
            doReturn(Optional.of(article)).when(articleRepository).findById(article.getId());

            when(articleLikeRepository.findByUserAndArticle(user, article)).thenReturn(Optional.of(articleDislike));

            // when
            ArticleLikeDto like = articleLikeService.likeOrDislike(user.getUsername(), article.getId(), false);

            // then
            verify(articleLikeRepository).delete(any(ArticleLike.class));
            assertNull(like);
        }

        @Test
        void AddDislikeWhenOriginalTypeIsTrue() {
            // given
            doReturn(Optional.of(user)).when(userRepository).findByUsername(user.getUsername());
            doReturn(Optional.of(article)).when(articleRepository).findById(article.getId());

            doReturn(articleDislike).when(articleLikeRepository).save(any(ArticleLike.class));

            // when
            ArticleLikeDto like = articleLikeService.likeOrDislike(user.getUsername(), article.getId(), false);

            // then
            assertThat(like.getType()).isEqualTo(false);
        }
    }
}
