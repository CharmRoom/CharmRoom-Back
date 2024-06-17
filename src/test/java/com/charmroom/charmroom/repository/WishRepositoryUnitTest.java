package com.charmroom.charmroom.repository;

import com.charmroom.charmroom.entity.*;
import com.charmroom.charmroom.entity.enums.BoardType;
import com.charmroom.charmroom.entity.enums.MarketArticleState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
@DisplayName("WishRepository Unit Test")
public class WishRepositoryUnitTest {
    @Autowired
    WishRepository wishRepository;
    @Autowired
    MarketRepository marketRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BoardRepository boardRepository;

    private Wish wish;

    @Autowired
    private ArticleRepository articleRepository;

    private Board createTestBoard() {
        return Board.builder()
                .type(BoardType.LIST)
                .exposed(false)
                .build();
    }

    private User createTestUser() {
        return User.builder()
                .id("1")
                .email("")
                .nickname("")
                .password("")
                .withdraw(false)
                .build();
    }

    private Article createTestArticle() {
        User user = createTestUser();
        userRepository.save(user);

        Board board = createTestBoard();
        boardRepository.save(board);

        return Article.builder()
                .user(user)
                .board(board)
                .title("")
                .body("")
                .view(0)
                .build();
    }

    private Market createTestMarket(Article article) {
        return Market.builder()
                .article(article)
                .price(1000)
                .state(MarketArticleState.SALE)
                .tag("")
                .build();
    }

    private Wish createTestWish(User user, Market market) {
        return Wish.builder()
                .user(user)
                .market(market)
                .build();
    }

    @BeforeEach
    void setUp() {
        User user = createTestUser();
        userRepository.save(user);
        Article article = createTestArticle();
        articleRepository.save(article);
        Market market = createTestMarket(article);
        marketRepository.save(market);

        wish = createTestWish(user, market);
    }

    @Nested
    @DisplayName("Create Wish")
    class CreateTestWish {
        @Test
        void success() {
            // given
            // when
            Wish saved = wishRepository.save(wish);
            // then
            assertThat(saved).isNotNull();
            assertThat(saved.getId()).isEqualTo(wish.getId());
        }
    }

    @Nested
    @DisplayName("Read Wish")
    class ReadTestWish {
        @Test
        void success() {
            // given
            Wish saved = wishRepository.save(wish);
            // when
            Wish found = wishRepository.findByUserAndMarket(saved.getUser(), saved.getMarket()).get();
            // then
            assertThat(found).isNotNull();
            assertThat(found).isEqualTo(saved);
        }

        @Test
        void fail_readWishWithDifferentMarket() {
            // given
            Wish saved = wishRepository.save(wish);

            Article article = Article.builder().title(".").body(".").view(1).build();
            articleRepository.save(article);

            Market different = Market.builder()
                    .price(2000)
                    .tag("test tag")
                    .article(article)
                    .build();
            marketRepository.save(different);
            // when
            Optional<Wish> found = wishRepository.findByUserAndMarket(saved.getUser(), different);
            // then
            assertThat(found).isNotPresent();
        }
    }

    @Nested
    @DisplayName("Delete Wish")
    class DeleteTestWish {
        @Test
        void success() {
            // given
            Wish saved = wishRepository.save(wish);
            // when
            wishRepository.delete(saved);
            List<Wish> found = wishRepository.findAll();
            // then
            assertThat(found).isNotNull();
            assertThat(found).isEmpty();
        }
    }
}
