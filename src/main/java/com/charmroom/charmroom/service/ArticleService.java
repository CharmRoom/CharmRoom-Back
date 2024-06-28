package com.charmroom.charmroom.service;

import java.util.List;

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

    public ArticleDto createArticle(User user, Board board, String title, String body, List<MultipartFile> fileList) {
        

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

    public Page<ArticleDto> getAllArticlesByPageable(Pageable pageable) {
        return articleRepository.findAll(pageable).map(article -> ArticleMapper.toDto(article));
    }

    public ArticleDto getOneArticle(Integer articleId) {
        Article found = articleRepository.findById(articleId)
                .orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_ARTICLE, "articleId: " + articleId)
                );
        return ArticleMapper.toDto(found);
    }

    @Transactional
    public ArticleDto updateArticle(Integer articleId, String username, String title, String body) {
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_USER, "username: " + username));

        Article originalArticle = articleRepository.findById(articleId).orElseThrow(
                () -> new BusinessLogicException(BusinessLogicError.NOTFOUND_ARTICLE, "articleId: " + articleId)
        );

        if (!originalArticle.getUser().equals(user)) {
            throw new BusinessLogicException(BusinessLogicError.UNAUTHORIZED_ARTICLE, "articleId: " + articleId);
        }
        originalArticle.updateTitle(title);
        originalArticle.updatedBody(body);

        return ArticleMapper.toDto(originalArticle);
    }

    public void deleteArticle(Integer articleId) {
        Article found = articleRepository.findById(articleId).orElseThrow(
                () -> new BusinessLogicException(BusinessLogicError.NOTFOUND_ARTICLE, "articleId: " + articleId)
        );

        articleRepository.delete(found);
    }
}
