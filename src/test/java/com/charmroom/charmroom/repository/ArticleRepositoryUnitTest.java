package com.charmroom.charmroom.repository;

import com.charmroom.charmroom.entity.Article;
import com.charmroom.charmroom.entity.Board;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.entity.enums.BoardType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@TestPropertySource(properties = {"spring.config.location = classpath:application-test.yml"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("ArticleRepository Unit Test")
public class ArticleRepositoryUnitTest {
    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BoardRepository boardRepository;

    Article article;

    private User createTestUser(String username) {
        return User.builder().username(username).email(username + "@test.com").nickname(username).password("").withdraw(false).build();
    }

    private Board createTestBoard() {
        return Board.builder().type(BoardType.LIST).exposed(false).build();
    }

    private Article createTestArticle(String username) {
        User user = createTestUser(username);
        userRepository.save(user);
        Board board = createTestBoard();
        boardRepository.save(board);
        return Article.builder().user(user).board(board).title("").body("").view(0).build();
    }

    @BeforeEach
    void setup() {
        article = createTestArticle("1");
    }

    @Nested
    @DisplayName("Create Article")
    class createTestArticle {
        @Test
        void success() {
            // given
            // when
            Article savedArticle = articleRepository.save(article);

            // then
            assertThat(savedArticle).isNotNull();
            assertThat(savedArticle.getId()).isEqualTo(article.getId());
            assertThat(savedArticle.getBody()).isEqualTo(article.getBody());
        }

        @Test
        void fail_whenSaveArticleWithNullTitle() {
            // given
            article.updateTitle(null);

            // when
            // then
            assertThrows(Exception.class, () -> articleRepository.save(article));
        }
    }

    @Nested
    @DisplayName("Read Article")
    class readTestArticle {
        @Test
        void success() {
            // given
            Article savedArticle = articleRepository.save(article);

            // when
            Article pickedArticle = articleRepository.findById(savedArticle.getId()).get();

            // then
            assertThat(pickedArticle).isNotNull();
            assertThat(pickedArticle).isEqualTo(savedArticle);
            assertThat(pickedArticle.getTitle()).isEqualTo(savedArticle.getTitle());
        }

        @Test
        void fail_FindByNonexistentId() {
            // given
            articleRepository.save(article);
            Integer nonexistentId = 999;

            // when
            Optional<Article> nonexistentArticle = articleRepository.findById(nonexistentId);

            // then
            assertThat(nonexistentArticle).isNotPresent();
        }
    }

    @Nested
    @DisplayName("Updated Article")
    class updateTestArticle {
        @Test
        void success() {
            // given
            Article savedArticle = articleRepository.save(article);
            String newTitle = "New Title";
            String newBody = "New Body";

            // when
            savedArticle.updateTitle(newTitle);
            savedArticle.updatedBody(newBody);

            // then
            assertThat(savedArticle.getTitle()).isEqualTo(newTitle);
            assertThat(savedArticle.getBody()).isEqualTo(newBody);
        }

        @Test
        void fail_whenUpdateTitleToNull() {
            // given
            article.updateTitle(null);

            // when
            // then
            assertThrows(Exception.class, () -> articleRepository.save(article));
        }
    }

    @Nested
    @DisplayName("Delete Article")
    class deleteTestArticle {
        @Test
        void success() {
            // given
            Article article1 = articleRepository.save(createTestArticle("A"));
            articleRepository.save(createTestArticle("B"));
            articleRepository.save(createTestArticle("C"));

            // when
            articleRepository.delete(article1);
            List<Article> articleList = articleRepository.findAll();

            // then
            assertThat(articleList).hasSize(2);
            assertThat(articleList).doesNotContain(article1);
        }

        @Test
        void fail_whenDeleteNonExistentArticle() {
            // given
            articleRepository.save(article);
            Integer nonExistentId = 999;

            // when
            // then
            assertThrows(Exception.class, () -> {
                if (!articleRepository.existsById(nonExistentId)) {
                    throw new Exception("Article not found");
                }
                articleRepository.deleteById(nonExistentId);
            });
        }

    }
}
