package com.charmroom.charmroom.repository;

import com.charmroom.charmroom.entity.Article;
import com.charmroom.charmroom.entity.Board;
import com.charmroom.charmroom.entity.Market;
import com.charmroom.charmroom.entity.User;
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
@DisplayName("MarketRepository Unit Test")
public class MarketRepositoryUnitTest {
    @Autowired
    MarketRepository marketRepository;
    @Autowired
    ArticleLikeRepository articleLikeRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BoardRepository boardRepository;

    private Market market;
    private Article article;
    @Autowired
    private ArticleRepository articleRepository;

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

    private Market createTestMarket(Article article) {
        return Market.builder().article(article).price(1000).state(MarketArticleState.SALE).tag("").build();
    }

    @BeforeEach
    void setUp() {
        article = createTestArticle("1");
        Article saved = articleRepository.save(article);
        market = createTestMarket(saved);
    }

    @Nested
    @DisplayName("Create Market Article")
    class createMarketArticle {
        @Test
        void success() {
            // given
            // when
            Market saved = marketRepository.save(market);
            // then
            assertThat(saved).isNotNull();
            assertThat(saved).isEqualTo(market);
            assertThat(saved.getArticle()).isEqualTo(article);
        }
    }

    @Nested
    @DisplayName("Read Market Article")
    class readMarketArticle {
        @Test
        void success() {
            // given
            Article article1 = createTestArticle("2");
            articleRepository.save(article1);
            Article article2 = createTestArticle("3");
            articleRepository.save(article2);
            Market saved1 = marketRepository.save(createTestMarket(article1));
            marketRepository.save(createTestMarket(article2));

            // when
            List<Market> founds = marketRepository.findAll();

            // then
            assertThat(founds).hasSize(2);
            assertThat(founds.get(0)).isEqualTo(saved1);
        }

        @Test
        void fail_readMarketWithWrongId() {
            // given
            marketRepository.save(market);
            Integer wrongMarketId = 999;
            // when
            Optional<Market> found = marketRepository.findById(wrongMarketId);
            // then
            assertThat(found).isNotPresent();
        }
    }

    @Nested
    @DisplayName("Update Market Article")
    class updateMarketArticle {
        @Test
        void success() {
            // given
            Market saved = marketRepository.save(market);

            // when
            saved.updatePrice(1000);
            saved.updateState(MarketArticleState.SOLD);
            saved.updateTag("test tag");

            // then
            assertThat(saved.getState()).isEqualTo(MarketArticleState.SOLD);
            assertThat(saved.getPrice()).isEqualTo(1000);
            assertThat(saved.getTag()).isEqualTo("test tag");
        }
    }

    @Nested
    @DisplayName("Delete Market Article")
    class deleteMarketArticle {
        @Test
        void success_deleteSomeMarketArticles() {
            // give
            Article article1 = createTestArticle("a");
            articleRepository.save(article1);
            Article article2 = createTestArticle("b");
            articleRepository.save(article2);
            Article article3 = createTestArticle("c");
            articleRepository.save(article3);
            Market saved1 = marketRepository.save(createTestMarket(article1));
            Market saved2 = marketRepository.save(createTestMarket(article2));
            marketRepository.save(createTestMarket(article3));

            // when
            marketRepository.delete(saved1);
            marketRepository.delete(saved2);
            List<Market> found = marketRepository.findAll();

            // then
            assertThat(found).isNotNull();
            assertThat(found).hasSize(1);
        }

        @Test
        void success_deleteOneMarketArticle() {
            // given
            Market saved = marketRepository.save(market);

            // when
            marketRepository.delete(saved);
            List<Market> found = marketRepository.findAll();

            // then
            assertThat(found).isNotNull();
            assertThat(found).isEmpty();
        }
    }
}
