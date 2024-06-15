package com.charmroom.charmroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.charmroom.charmroom.entity.Board;

public interface BoardRepository extends JpaRepository<Board, Integer> {
	
}
