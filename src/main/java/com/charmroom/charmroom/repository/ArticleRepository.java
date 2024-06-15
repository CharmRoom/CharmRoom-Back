package com.charmroom.charmroom.repository;

import com.charmroom.charmroom.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Integer> {
}
