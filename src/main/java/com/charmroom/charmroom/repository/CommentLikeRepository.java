package com.charmroom.charmroom.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.charmroom.charmroom.entity.Comment;
import com.charmroom.charmroom.entity.CommentLike;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.entity.embid.CommentLikeId;

public interface CommentLikeRepository extends JpaRepository<CommentLike, CommentLikeId> {
	Optional<CommentLike> findByUserAndComment(User user, Comment comment);
}
