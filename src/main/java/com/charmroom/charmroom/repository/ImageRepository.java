package com.charmroom.charmroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.charmroom.charmroom.entity.Image;

public interface ImageRepository extends JpaRepository<Image, Integer> {
	
}
