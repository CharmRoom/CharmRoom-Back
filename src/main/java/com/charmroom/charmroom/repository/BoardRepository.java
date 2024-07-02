package com.charmroom.charmroom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.charmroom.charmroom.entity.Board;

public interface BoardRepository extends JpaRepository<Board, Integer> {
	Boolean existsByName(String name);
	List<Board> findAllByExposed(boolean b);
}
