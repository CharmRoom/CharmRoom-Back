package com.charmroom.charmroom.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.charmroom.charmroom.entity.Article;
import com.charmroom.charmroom.entity.Comment;
import com.charmroom.charmroom.entity.User;

public interface CommentRepository extends JpaRepository<Comment, Integer>{

	Page<Comment> findAllByArticle(Article article, Pageable pageable);
	Page<Comment> findAllByUser(User user, Pageable pageable);
}
