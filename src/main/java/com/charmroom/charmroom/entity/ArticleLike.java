package com.charmroom.charmroom.entity;

import com.charmroom.charmroom.entity.embid.ArticleLikeId;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Builder
public class ArticleLike {
    @EmbeddedId
    private ArticleLikeId id;

    @MapsId(value = "articleId")
    @ManyToOne
    private Article article;

    @MapsId(value = "userId")
    @ManyToOne
    private User user;

    @Column(nullable = false)
    private boolean type;

    public ArticleLike(ArticleLikeId id, Article article, User user, boolean type) {
        this.id = new ArticleLikeId(article.getId(), user.getId());
        this.article = article;
        this.user = user;
        this.type = type;
    }
}
