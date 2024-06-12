package com.charmroom.charmroom.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PROTECTED)
@Entity
@Getter
@Builder
@AllArgsConstructor
public class CommentLike {
	@EmbeddedId
	private CommentLikeId id;
	
	@MapsId(value = "userId")
	@ManyToOne
	private User user;
	
	@MapsId(value = "commentId")
	@ManyToOne
	private Comment comment;
	
	private Boolean type;
}
