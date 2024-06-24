package com.charmroom.charmroom.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.charmroom.charmroom.entity.Article;
import com.charmroom.charmroom.entity.Attachment;
import com.charmroom.charmroom.entity.Board;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.repository.ArticleRepository;
import com.charmroom.charmroom.repository.AttachmentRepository;
import com.charmroom.charmroom.util.CharmroomUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final AttachmentRepository attachmentRepository;
    private final CharmroomUtil.Upload uploadUtil;

    public Article createArticle(User user, Board board, String title, String body, List<MultipartFile> fileList) {
        

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
        
        return articleRepository.save(article);
    }

    public Page<Article> getAllArticlesByPageable(Pageable pageable) {
        return articleRepository.findAll(pageable);
    }

    public Article getOneArticle(Integer articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_ARTICLE, "articleId: " + articleId)
                );
    }

    @Transactional
    public Article updateArticle(Integer articleId, Article updated) {
        Article originalArticle = articleRepository.findById(articleId).orElseThrow(
                () -> new BusinessLogicException(BusinessLogicError.NOTFOUND_ARTICLE, "articleId: " + articleId)
        );
        originalArticle.updateTitle(updated.getTitle());
        originalArticle.updatedBody(updated.getBody());

        return originalArticle;
    }

    public void deleteArticle(Integer articleId) {
        Article found = articleRepository.findById(articleId).orElseThrow(
                () -> new BusinessLogicException(BusinessLogicError.NOTFOUND_ARTICLE, "articleId: " + articleId)
        );

        articleRepository.delete(found);
    }
}
