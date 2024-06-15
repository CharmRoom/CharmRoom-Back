package com.charmroom.charmroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.charmroom.charmroom.entity.Article;

public interface ArticleRepository extends JpaRepository<Article, Integer> {
	
}
