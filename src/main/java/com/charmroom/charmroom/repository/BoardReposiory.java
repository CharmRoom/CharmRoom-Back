package com.charmroom.charmroom.repository;

import com.charmroom.charmroom.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardReposiory extends JpaRepository<Board, Integer> {
}
