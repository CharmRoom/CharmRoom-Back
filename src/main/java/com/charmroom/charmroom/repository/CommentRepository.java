package com.charmroom.charmroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.charmroom.charmroom.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Integer>{
	
}
