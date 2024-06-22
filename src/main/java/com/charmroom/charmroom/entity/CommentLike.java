package com.charmroom.charmroom.entity;

import com.charmroom.charmroom.entity.embid.CommentLikeId;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PROTECTED)
@Entity
@Getter
@Builder
public class CommentLike {

	@EmbeddedId
	private CommentLikeId id;
	
	@MapsId(value = "userId")
	@ManyToOne
	private User user;
	
	@MapsId(value = "commentId")
	@ManyToOne
	private Comment comment;
	
	@Builder.Default
	private Boolean type = true;
	
	public CommentLike(CommentLikeId id, User user, Comment comment, Boolean type) {
		this.id = new CommentLikeId(user.getId(), comment.getId());
		this.user = user;
		this.comment = comment;
		this.type = type;
	}
	
	public void updateType(Boolean type) {
		this.type = type;
	}
}
