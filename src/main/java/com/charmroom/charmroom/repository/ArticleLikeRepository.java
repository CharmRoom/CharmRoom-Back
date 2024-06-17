package com.charmroom.charmroom.repository;

import com.charmroom.charmroom.entity.Article;
import com.charmroom.charmroom.entity.ArticleLike;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.entity.embid.ArticleLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleLikeRepository extends JpaRepository<ArticleLike, ArticleLikeId> {
    Optional<ArticleLike> findByUserAndArticle(User user, Article article);
}
