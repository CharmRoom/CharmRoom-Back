package com.charmroom.charmroom.service;

import com.charmroom.charmroom.dto.business.MarketDto;
import com.charmroom.charmroom.entity.Article;
import com.charmroom.charmroom.entity.Attachment;
import com.charmroom.charmroom.entity.Board;
import com.charmroom.charmroom.entity.Market;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.entity.enums.MarketArticleState;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.repository.ArticleRepository;
import com.charmroom.charmroom.repository.AttachmentRepository;
import com.charmroom.charmroom.repository.BoardRepository;
import com.charmroom.charmroom.repository.MarketRepository;
import com.charmroom.charmroom.repository.UserRepository;
import com.charmroom.charmroom.util.CharmroomUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MarketServiceUnitTest {
    @Mock
    MarketRepository marketRepository;
    @Mock
    ArticleRepository articleRepository;
    @Mock
    AttachmentRepository attachmentRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    BoardRepository boardRepository;
    @Mock
    CharmroomUtil.Upload uploadUtils;
    @InjectMocks
    MarketService marketService;

    private Market market;
    private User user;
    private Board board;
    private Article article;
    private int price;
    private String tag;
    private int marketId;
    private MarketArticleState state;

    Board createBoard() {
        return Board.builder()
                .id(1)
                .build();
    }

    User createUser() {
        return User.builder()
                .username("")
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

    Market createMarket(int prefix) {
        return Market.builder()
                .id(prefix + marketId)
                .article(article)
                .price(price)
                .tag(tag)
                .state(state)
                .build();
    }

    @BeforeEach
    void setUp() {
        price = 10000;
        tag = "new product";
        marketId = 0;
        state = MarketArticleState.SALE;
        board = createBoard();
        user = createUser();
        article = createArticle();
        market = createMarket(0);
    }

    @Nested
    @DisplayName("Create Market")
    class CreateMarket {
        @Test
        void success() {
            // given
            doReturn(Optional.of(user)).when(userRepository).findByUsername(user.getUsername());
            doReturn(Optional.of(board)).when(boardRepository).findById(board.getId());
            doReturn(article).when(articleRepository).save(any(Article.class));

            MarketDto marketDto = MarketDto.builder()
                    .username(user.getUsername())
                    .boardId(board.getId())
                    .title("")
                    .body("")
                    .price(price)
                    .tag(tag)
                    .state(state)
                    .build();

            doReturn(market).when(marketRepository).save(any(Market.class));

            // when
            Market created = marketService.create(marketDto);

            // then
            assertThat(created).isNotNull();
        }

        @Test
        void whenFilesExists() {
            // given
            doReturn(Optional.of(user)).when(userRepository).findByUsername(user.getUsername());
            doReturn(Optional.of(board)).when(boardRepository).findById(board.getId());
            doReturn(article).when(articleRepository).save(any(Article.class));

            MarketDto marketDto = MarketDto.builder()
                    .username(user.getUsername())
                    .boardId(board.getId())
                    .title("")
                    .body("")
                    .price(price)
                    .tag(tag)
                    .state(state)
                    .build();

            Attachment attachment = Attachment.builder()
                    .build();

            List<MultipartFile> files = new ArrayList<>();

            MockMultipartFile file1 = new MockMultipartFile("test", "test.png", "image/png", "test".getBytes());
            MockMultipartFile file2 = new MockMultipartFile("test", "test.png", "image/png", "test".getBytes());

            files.add(file1);
            files.add(file2);

            doReturn(market).when(marketRepository).save(any(Market.class));

            doReturn(attachment).when(uploadUtils).buildAttachment(eq(file1), any(Article.class));
            doReturn(attachment).when(uploadUtils).buildAttachment(eq(file2), any(Article.class));

            doReturn(attachment).when(attachmentRepository).save(attachment);

            // when
            Market created = marketService.create(marketDto, files);

            // then
            assertThat(created).isNotNull();
        }
    }

    @Nested
    @DisplayName("Get Market List")
    class GetMarketList {
        @Test
        void success() {
            // given
            var marketList = List.of(createMarket(1), createMarket(2), createMarket(3));
            PageRequest pageRequest = PageRequest.of(0, 3, Sort.Direction.ASC, "id");
            var marketPage = new PageImpl<>(marketList);

            doReturn(marketPage).when(marketRepository).findAll(pageRequest);

            // when
            Page<Market> result = marketService.getAllMarketsByPageable(pageRequest);

            // then
            assertThat(result).hasSize(3);
        }
    }

    @Nested
    @DisplayName("Get Market")
    class GetMarket {
        @Test
        void success() {
            // given
            doReturn(Optional.of(market)).when(marketRepository).findById(marketId);

            // when
            Market found = marketService.getMarket(marketId);

            // then
            assertThat(found).isNotNull();
            assertThat(found.getId()).isEqualTo(marketId);
        }

        @Test
        void fail_NotFoundArticle() {
            // given
            doReturn(Optional.empty()).when(marketRepository).findById(marketId);

            // when
            BusinessLogicException thrown = assertThrows(BusinessLogicException.class, () -> marketService.getMarket(marketId));

            // then
            assertThat(thrown.getError()).isEqualTo(BusinessLogicError.NOTFOUND_ARTICLE);
        }
    }

    @Nested
    @DisplayName("Update Price")
    class UpdatePrice {
        @Test
        void success() {
            // given
            doReturn(Optional.of(market)).when(marketRepository).findById(marketId);

            // when
            Market updated = marketService.updatePrice(marketId, 20000);

            // then
            assertThat(updated).isNotNull();
            assertThat(updated.getPrice()).isEqualTo(20000);
        }
    }

    @Nested
    @DisplayName("Update Tag")
    class UpdateTag {
        @Test
        void success() {
            // given
            doReturn(Optional.of(market)).when(marketRepository).findById(marketId);

            // when
            Market updated = marketService.updateTag(marketId, "new tag");

            // then
            assertThat(updated).isNotNull();
            assertThat(updated.getTag()).isEqualTo("new tag");
        }
    }

    @Nested
    @DisplayName("Update State")
    class UpdateState {
        @Test
        void success() {
            // given
            doReturn(Optional.of(market)).when(marketRepository).findById(marketId);
            MarketArticleState newState = MarketArticleState.RESERVED;

            // when
            Market updated = marketService.updateState(marketId, newState);

            // then
            assertThat(updated).isNotNull();
            assertThat(updated.getState()).isEqualTo(newState);
        }
    }

    @Nested
    @DisplayName("Delete Market")
    class DeleteMarket {
        @Test
        void success() {
            // given
            doReturn(Optional.of(market)).when(marketRepository).findById(marketId);

            // when
            marketService.delete(marketId);

            // then
            verify(marketRepository).delete(market);
            verify(articleRepository).delete(article);
        }

        @Test
        void fail_NotFoundArticle() {
            // given
            doReturn(Optional.empty()).when(marketRepository).findById(marketId);

            // when
            BusinessLogicException thrown = assertThrows(BusinessLogicException.class, () -> marketService.delete(marketId));

            // then
            assertThat(thrown.getError()).isEqualTo(BusinessLogicError.NOTFOUND_ARTICLE);

        }
    }
}
