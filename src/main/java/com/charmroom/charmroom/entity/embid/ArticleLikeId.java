package com.charmroom.charmroom.entity.embid;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class ArticleLikeId implements Serializable {
    private static final long serialVersionUID = 23453463533336235L;

    private Integer articleId;
    private Integer userId;
}
