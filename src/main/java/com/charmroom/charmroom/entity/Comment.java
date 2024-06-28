package com.charmroom.charmroom.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
public class Comment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	private User user;
	
	@ManyToOne
	private Article article;
	
	@ManyToOne
	private Comment parent;
	
	@OneToMany(mappedBy = "parent")
	private List<Comment> childList;
	
	@Column(columnDefinition = "TEXT")
	private String body;
	
	@CreationTimestamp
	private LocalDateTime createdAt;
	
	@UpdateTimestamp
	private LocalDateTime updatedAt;
	
	@Builder.Default
	@Column(nullable = false)
	private Boolean disabled = false;
	
	@Builder.Default
	@OneToMany(mappedBy="comment")
	private List<CommentLike> commentLike = new ArrayList<>();
	
	public void updateBody(String body) {
		this.body = body;
	}
	
	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}
}
