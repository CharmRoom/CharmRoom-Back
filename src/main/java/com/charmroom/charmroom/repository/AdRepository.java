package com.charmroom.charmroom.repository;

import com.charmroom.charmroom.entity.Ad;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AdRepository extends JpaRepository<Ad, Integer> {
	List<Ad> findByStartBeforeAndEndAfter(LocalDateTime now1, LocalDateTime now2);
}
