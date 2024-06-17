package com.charmroom.charmroom.repository;

import com.charmroom.charmroom.entity.Ad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdRepository extends JpaRepository<Ad, Integer> {
}
