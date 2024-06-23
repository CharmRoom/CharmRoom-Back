package com.charmroom.charmroom.service;

import com.charmroom.charmroom.entity.Article;
import com.charmroom.charmroom.entity.Board;
import com.charmroom.charmroom.entity.Market;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.entity.Wish;
import com.charmroom.charmroom.entity.enums.MarketArticleState;
import com.charmroom.charmroom.repository.MarketRepository;
import com.charmroom.charmroom.repository.UserRepository;
import com.charmroom.charmroom.repository.WishRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class WishServiceUnitTest {
    @Mock
    private WishRepository wishRepository;
    @Mock
    private MarketRepository marketRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private WishService wishService;

    private Wish wish;
    private Market market;
    private Article article;
    private User user;
    private Board board;
    private String username;
    private Integer marketId;

    User cresteUser() {
        return User.builder()
                .username(username)
                .nickname("nickname")
                .email("email@example.com")
                .build();
    }

    Article createArticle() {
        return Article.builder()
                .title("title")
                .body("body")
                .user(user)
                .board(board)
                .build();
    }

    Market createMarket() {
        return Market.builder()
                .id(marketId)
                .tag("")
                .price(1000)
                .state(MarketArticleState.SALE)
                .article(article)
                .build();
    }

    Wish createWish() {
        return Wish.builder()
                .market(market)
                .user(user)
                .build();
    }

    @BeforeEach
    void setUp() {
        username = "username";
        marketId = 1;
        user = cresteUser();
        article = createArticle();
        market = createMarket();
        wish = createWish();
    }

    @Nested
    @DisplayName("Create Wish")
    class CreateWish {
        @Test
        void success() {
            // given
            doReturn(Optional.of(user)).when(userRepository).findByUsername(username);
            doReturn(Optional.of(market)).when(marketRepository).findById(marketId);
            doReturn(wish).when(wishRepository).save(any(Wish.class));
            doReturn(Optional.empty()).when(wishRepository).findByUserAndMarket(user, market);

            // when
            Wish created = wishService.create(username, marketId);

            // then
            assertThat(created).isNotNull();
            assertThat(created.getUser()).isEqualTo(user);
        }

        @Test
        void whenExistingWish() {
            // given
            doReturn(Optional.of(user)).when(userRepository).findByUsername(username);
            doReturn(Optional.of(market)).when(marketRepository).findById(marketId);
            doReturn(Optional.of(wish)).when(wishRepository).findByUserAndMarket(user, market);

            // when
            Wish created = wishService.create(username, marketId);

            // then
            verify(wishRepository).delete(any(Wish.class));
            assertNull(created);
        }
    }
}
