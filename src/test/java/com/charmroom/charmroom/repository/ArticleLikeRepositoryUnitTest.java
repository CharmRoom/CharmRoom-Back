package com.charmroom.charmroom.repository;

import com.charmroom.charmroom.entity.Article;
import com.charmroom.charmroom.entity.ArticleLike;
import com.charmroom.charmroom.entity.Board;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.entity.enums.BoardType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {"spring.config.location = classpath:application-test.yml"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("ArticleLikeRepository Unit Test")
public class ArticleLikeRepositoryUnitTest {
    @Autowired
    ArticleLikeRepository articleLikeRepository;
    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BoardRepository boardRepository;

    private User user;
    private Article article;
    private ArticleLike articleLike;

    private Board createTestBoard() {
        return Board.builder().type(BoardType.LIST).exposed(false).build();
    }

    private User createTestUser(String username) {
        return User.builder().username(username).email(username + "@test.com").nickname(username).password("").withdraw(false).build();
    }

    private Article createTestArticle(String username) {
        User user = createTestUser(username);
        userRepository.save(user);
        Board board = createTestBoard();
        boardRepository.save(board);
        return Article.builder().user(user).board(board).title("").body("").view(0).build();
    }

    private ArticleLike createTestArticleLike() {
        return ArticleLike.builder().article(article).user(user).type(true).build();
    }

    @BeforeEach
    void setUp() {
        user = createTestUser("1");
        userRepository.save(user);

        article = createTestArticle("2");
        articleRepository.save(article);

        articleLike = createTestArticleLike();
    }

    @Nested
    @DisplayName("Create ArticleLike")
    class CreateArticleLike {
        @Test
        void success() {
            // given
            // when
            ArticleLike savedArticleLike = articleLikeRepository.save(articleLike);

            // then
            assertThat(savedArticleLike).isNotNull();
            assertThat(savedArticleLike.getId()).isEqualTo(articleLike.getId());
        }
    }

    @Nested
    @DisplayName("Read ArticleLike")
    class ReadArticleLike {
        @Test
        void success() {
            // given
            ArticleLike saved = articleLikeRepository.save(articleLike);

            // when
            ArticleLike found = articleLikeRepository.findByUserAndArticle(user, article).get();

            // then
            assertThat(found).isNotNull();
            assertThat(found).isEqualTo(saved);
        }

        @Test
        void fail_ReadArticleLikeWithWrongUser() {
            // given
            articleLikeRepository.save(articleLike);
            User _user = User.builder().username("3").email("").nickname("").password("").withdraw(false).build();
            User savedUser = userRepository.save(_user);

            // when
            Optional<ArticleLike> found = articleLikeRepository.findByUserAndArticle(savedUser, article);

            // then
            assertThat(found).isNotPresent();
        }
    }

    @Nested
    @DisplayName("Delete ArticleLike")
    class DeleteArticleLike {
        @Test
        void success() {
            // given
            ArticleLike saved = articleLikeRepository.save(articleLike);

            // when
            articleLikeRepository.delete(saved);
            List<ArticleLike> found = articleLikeRepository.findAll();

            // then
            assertThat(found).isNotNull();
            assertThat(found).hasSize(0);
        }
    }
}
