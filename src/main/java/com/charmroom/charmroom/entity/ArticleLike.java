package com.charmroom.charmroom.entity;

import com.charmroom.charmroom.entity.embid.ArticleLikeId;
import com.charmroom.charmroom.entity.embid.CommentLikeId;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Builder
@AllArgsConstructor
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
}
