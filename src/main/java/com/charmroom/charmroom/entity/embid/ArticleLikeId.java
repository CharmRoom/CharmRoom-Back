package com.charmroom.charmroom.entity.embid;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.io.Serializable;

@Embeddable
@Getter
public class ArticleLikeId implements Serializable {
    private static final long serialVersionUID = 23453463533336235L;

    private Integer articleId;
    private String userId;
}
