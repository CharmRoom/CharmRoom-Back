package com.charmroom.charmroom.service;

import com.charmroom.charmroom.dto.business.ArticleLikeDto;
import com.charmroom.charmroom.dto.business.ArticleLikeMapper;
import com.charmroom.charmroom.entity.Article;
import com.charmroom.charmroom.entity.ArticleLike;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.repository.ArticleLikeRepository;
import com.charmroom.charmroom.repository.ArticleRepository;
import com.charmroom.charmroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticleLikeService {
    private final ArticleLikeRepository articleLikeRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;

    @Transactional
    public ArticleLikeDto likeOrDislike(String username, Integer articleId, Boolean type) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_USER));
        Article article = articleRepository.findById(articleId).orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_ARTICLE));

        Optional<ArticleLike> found = articleLikeRepository.findByUserAndArticle(user, article);

        if (found.isPresent()) {
            ArticleLike articleLike = found.get();
            if (articleLike.getType() == type) {
                articleLikeRepository.delete(articleLike);
                return null; // 좋아요/싫어요 취소
            } else {
                articleLike.changeType(type);
                return ArticleLikeMapper.toDto(articleLike);
            }
        } else {
            ArticleLike articleLike = ArticleLike.builder()
                    .article(article)
                    .user(user)
                    .type(type)
                    .build();

            ArticleLike saved = articleLikeRepository.save(articleLike);
            return ArticleLikeMapper.toDto(saved);
        }
    }

    public ArticleLikeDto like(String username, Integer articleId) {
        return likeOrDislike(username, articleId, true);
    }

    public ArticleLikeDto dislike(String username, Integer articleId) {
        return likeOrDislike(username, articleId, false);
    }
}
