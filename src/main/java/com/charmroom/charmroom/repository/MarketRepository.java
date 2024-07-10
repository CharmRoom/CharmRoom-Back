package com.charmroom.charmroom.repository;

import com.charmroom.charmroom.entity.Board;
import com.charmroom.charmroom.entity.Market;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MarketRepository extends JpaRepository<Market, Integer> {
    @Query(value = "select m from Market m, Article a where m.article = a and a.board = :board", countQuery = "select count(m) from Market m, Article a where m.article = a and a.board = :board")
    Page<Market> findAllByBoard(Board board, Pageable pageable);
}
