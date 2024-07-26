package com.charmroom.charmroom.service;

import java.util.ArrayList;
import java.util.List;

import com.charmroom.charmroom.repository.BoardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.charmroom.charmroom.dto.business.ArticleDto;
import com.charmroom.charmroom.dto.business.ArticleMapper;
import com.charmroom.charmroom.entity.Article;
import com.charmroom.charmroom.entity.Attachment;
import com.charmroom.charmroom.entity.Board;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.repository.ArticleRepository;
import com.charmroom.charmroom.repository.AttachmentRepository;
import com.charmroom.charmroom.repository.UserRepository;
import com.charmroom.charmroom.util.CharmroomUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final AttachmentRepository attachmentRepository;
    private final CharmroomUtil.Upload uploadUtil;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    public ArticleDto createArticle(String username, Integer boardId, String title, String body, List<MultipartFile> fileList) {
        User user = getUser(username);
        Board board = getBoard(boardId);

        Article article = Article.builder()
                .title(title)
                .body(body)
                .user(user)
                .board(board)
                .build();
        for (MultipartFile attachment : fileList) {
            Attachment attachmentEntity = uploadUtil.buildAttachment(attachment, article);
            Attachment saved = attachmentRepository.save(attachmentEntity);
            article.getAttachmentList().add(saved);
        }

        Article saved = articleRepository.save(article);
        return ArticleMapper.toDto(saved);
    }

    public ArticleDto createArticle(String username, Integer boardId, String title, String body) {
        return createArticle(username, boardId, title, body, new ArrayList<>());
    }

    public ArticleDto getOneArticle(Integer articleId) {
        Article found = getArticle(articleId);
        return ArticleMapper.toDto(found);
    }

    public Page<ArticleDto> getArticles(Integer boardId, Pageable pageable) {
        Board board = getBoard(boardId);
        Page<Article> articles = articleRepository.findAllByBoard(board, pageable);
        return articles.map(ArticleMapper::toDto);
    }

    public Page<ArticleDto> getArticlesByUsername(String username, Pageable pageable) {
        User user = getUser(username);
        Page<Article> articles = articleRepository.findAllByUser(user, pageable);
        return articles.map(ArticleMapper::toDto);
    }

    @Transactional
    public ArticleDto updateArticle(Integer articleId, String username, String title, String body) {
        Article originalArticle = getArticle(articleId);
        if(!username.equals(originalArticle.getUser().getUsername())) {
            throw new BusinessLogicException(BusinessLogicError.UNAUTHORIZED_ARTICLE, "articleId: " + articleId);
        }

        originalArticle.updateTitle(title);
        originalArticle.updatedBody(body);
        return ArticleMapper.toDto(originalArticle);
    }

    public void deleteArticle(Integer articleId, String username) {
        Article article = getArticle(articleId);
        if(!username.equals(article.getUser().getUsername())) {
            throw new BusinessLogicException(BusinessLogicError.UNAUTHORIZED_ARTICLE, "articleId: " + articleId);
        }
        articleRepository.delete(article);
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_USER, "username: " + username));
    }

    private Board getBoard(Integer boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_BOARD, "boardId: " + boardId));
    }

    private Article getArticle(Integer articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_ARTICLE, "articleId: " + articleId));
    }
}
