package com.charmroom.charmroom.repository;

import com.charmroom.charmroom.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.charmroom.charmroom.entity.Article;
import com.charmroom.charmroom.entity.Board;

public interface ArticleRepository extends JpaRepository<Article, Integer> {
	Page<Article> findAllByBoard(Board board, Pageable pageable);

    Page<Article> findAllByUser(User user, Pageable pageable);
}
