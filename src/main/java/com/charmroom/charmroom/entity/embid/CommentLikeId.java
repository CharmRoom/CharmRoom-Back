package com.charmroom.charmroom.entity.embid;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class CommentLikeId implements Serializable {
	private static final long serialVersionUID = -782649823267495831L;
	private String userId;
	private Integer commentId;
}
