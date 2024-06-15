package com.charmroom.charmroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.charmroom.charmroom.entity.Point;

public interface PointRepository extends JpaRepository<Point, Integer> {

}
