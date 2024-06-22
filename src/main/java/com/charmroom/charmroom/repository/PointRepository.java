package com.charmroom.charmroom.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.charmroom.charmroom.entity.Point;
import com.charmroom.charmroom.entity.User;

public interface PointRepository extends JpaRepository<Point, Integer> {
	Page<Point> findAllByUser(User user, Pageable pageable);
}
