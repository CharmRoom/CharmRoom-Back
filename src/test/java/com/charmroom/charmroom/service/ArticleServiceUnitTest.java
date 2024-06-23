package com.charmroom.charmroom.service;

import com.charmroom.charmroom.entity.Article;
import com.charmroom.charmroom.entity.Attachment;
import com.charmroom.charmroom.entity.Board;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.entity.enums.AttachmentType;
import com.charmroom.charmroom.entity.enums.BoardType;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.repository.ArticleRepository;
import com.charmroom.charmroom.repository.AttachmentRepository;
import com.charmroom.charmroom.util.CharmroomUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceUnitTest {
    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private AttachmentRepository attachmentRepository;
    @Mock
    private CharmroomUtil.Upload uploadUtil;
    @Spy
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @InjectMocks
    private ArticleService articleService;

    private Article article;
    private User user;
    private Board board;
    private String title;
    private String body;
    private List<MultipartFile> fileList;
    private Attachment attachment1;
    private Attachment attachment2;
    private Attachment attachment3;
    private MultipartFile multipartFile1;
    private MultipartFile multipartFile2;
    private MultipartFile multipartFile3;

    private User createUser() {
        return User.builder()
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

    private Article createArticle(String prefix) {
        user = createUser();
        board = createBoard();

        return Article.builder()
                .title(prefix + title)
                .body(prefix + body)
                .user(user)
                .board(board)
                .build();
    }

    private Attachment createAttachment(String prefix) {
        return Attachment.builder()
                .path(prefix + "path")
                .type(AttachmentType.IMAGE)
                .originalName(prefix + "originalName")
                .build();
    }

    @BeforeEach
    void setUp() {
        int articleId = 1;
        title = "test title";
        body = "test body";
        user = createUser();
        board = createBoard();

        multipartFile1 = new MockMultipartFile("file", "test.png", "image/png", "test".getBytes());
        multipartFile2 = new MockMultipartFile("file", "test.png", "image/png", "test".getBytes());
        multipartFile3 = new MockMultipartFile("file", "test.png", "image/png", "test".getBytes());

        attachment1 = createAttachment("attachment1");
        attachment2 = createAttachment("attachment2");
        attachment3 = createAttachment("attachment3");
        fileList = List.of(multipartFile1, multipartFile2, multipartFile3);
        List<Attachment> attachmentList = List.of(attachment1, attachment2, attachment3);

        article = Article.builder()
                .id(articleId)
                .title(title)
                .body(body)
                .board(board)
                .user(user)
                .attachmentList(attachmentList)
                .build();
    }

    @Nested
    @DisplayName("Create Article")
    class createArticle {
        @Test
        void success() {
            // given
            doReturn(attachment1).when(uploadUtil).buildAttachment(multipartFile1);
            doReturn(attachment2).when(uploadUtil).buildAttachment(multipartFile2);
            doReturn(attachment3).when(uploadUtil).buildAttachment(multipartFile3);
            doReturn(attachment1).when(attachmentRepository).save(attachment1);
            doReturn(attachment2).when(attachmentRepository).save(attachment2);
            doReturn(attachment3).when(attachmentRepository).save(attachment3);
            doReturn(article).when(articleRepository).save(any(Article.class));

            // when
            Article saved = articleService.createArticle(user, board, title, body, fileList);

            // then
            verify(articleRepository).save(any(Article.class));
            assertThat(saved).isNotNull();
            assertThat(saved.getBody().equals(article.getBody())).isTrue();
        }
    }

    @Nested
    @DisplayName("Read ArticleList")
    class readArticleList {
        @Test
        void success() {
            // given
            List<Article> articleList = List.of(createArticle("1"), createArticle("2"), createArticle("3"));
            PageRequest pageRequest = PageRequest.of(0, 3, Sort.Direction.ASC, "title");
            PageImpl<Article> articlePage = new PageImpl<>(articleList);

            doReturn(articlePage).when(articleRepository).findAll(pageRequest);

            // when
            Page<Article> articles = articleService.getAllArticlesByPageable(pageRequest);

            // then
            verify(articleRepository).findAll(pageRequest);
            assertThat(articles).hasSize(3);
            assertThat(articles.stream().toList().get(0)).isEqualTo(articleList.get(0));
        }

        @Test
        void fail_noArticlesFound() {
            // given
            PageRequest pageRequest = PageRequest.of(0, 3, Sort.Direction.ASC, "title");
            PageImpl<Article> articlePage = new PageImpl<>(List.of());

            doReturn(articlePage).when(articleRepository).findAll(pageRequest);

            // when
            Page<Article> articles = articleService.getAllArticlesByPageable(pageRequest);

            // then
            verify(articleRepository).findAll(pageRequest);
            assertThat(articles).isEmpty();
        }
    }

    @Nested
    @DisplayName("Get One Article")
    class getOneArticle {
        @Test
        void success() {
            // given
            when(articleRepository.findById(article.getId())).thenReturn(Optional.of(article));

            // when
            Article found = articleService.getOneArticle(article.getId());

            // then
            verify(articleRepository).findById(article.getId());
            assertThat(found).isNotNull();
            assertThat(found.getTitle()).isEqualTo(article.getTitle());
        }

        @Test
        void fail_noArticleFound() {
            // given
            doReturn(Optional.empty()).when(articleRepository).findById(article.getId());

            // when
            BusinessLogicException thrown = assertThrows(BusinessLogicException.class, () ->
                    articleService.getOneArticle(article.getId()));

            // then
            assertThat(thrown.getError()).isEqualTo(BusinessLogicError.NOTFOUND_ARTICLE);
            assertThat(thrown.getMessage()).isEqualTo("articleId: " + article.getId());
        }
    }

    @Nested
    @DisplayName("Update Article")
    class updateArticle {
        @Test
        void success() {
            // given
            Article updated = Article.builder()
                    .title("updated")
                    .body("updated")
                    .build();

            doReturn(Optional.of(article)).when(articleRepository).findById(article.getId());

            // when
            Article updatedArticle = articleService.updateArticle(article.getId(), updated);

            // then
            verify(articleRepository).findById(article.getId());
            assertThat(updatedArticle.getTitle()).isEqualTo(updated.getTitle());
            assertThat(updatedArticle.getBody()).isEqualTo(updated.getBody());
        }

        @Test
        void fail_noArticleFound() {
            // given
            Article updated = Article.builder()
                    .title("updated")
                    .body("updated")
                    .build();

            doReturn(Optional.empty()).when(articleRepository).findById(article.getId());

            // when
            BusinessLogicException thrown = assertThrows(BusinessLogicException.class, () ->
                    articleService.updateArticle(article.getId(), updated)
            );

            // then
            verify(articleRepository).findById(article.getId());
            assertThat(thrown.getError()).isEqualTo(BusinessLogicError.NOTFOUND_ARTICLE);
            assertThat(thrown.getMessage()).isEqualTo("articleId: " + article.getId());
        }
    }

    @Nested
    @DisplayName("Delete Article")
    class deleteArticle {
        @Test
        void success() {
            // given
            doReturn(Optional.of(article)).when(articleRepository).findById(article.getId());

            // when
            articleService.deleteArticle(article.getId());

            // then
            verify(articleRepository).findById(article.getId());
            verify(articleRepository).delete(article);
        }

        @Test
        void fail_noArticleFound() {
            // given
            doReturn(Optional.empty()).when(articleRepository).findById(article.getId());

            // when
            BusinessLogicException thrown = assertThrows(BusinessLogicException.class, () ->
                    articleService.deleteArticle(article.getId()));

            // then
            verify(articleRepository).findById(article.getId());
            assertThat(thrown.getError()).isEqualTo(BusinessLogicError.NOTFOUND_ARTICLE);
            assertThat(thrown.getMessage()).isEqualTo("articleId: " + article.getId());
        }
    }
}
