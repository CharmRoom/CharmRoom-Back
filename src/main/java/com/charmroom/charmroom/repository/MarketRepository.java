package com.charmroom.charmroom.repository;

import com.charmroom.charmroom.entity.Board;
import com.charmroom.charmroom.entity.Market;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketRepository extends JpaRepository<Market, Integer> {
    Page<Market> findAllByBoard(Board board, Pageable pageable);
}
