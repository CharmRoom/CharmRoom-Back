package com.charmroom.charmroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.charmroom.charmroom.entity.Club;

import java.util.Optional;

public interface ClubRepository extends JpaRepository<Club, Integer> {
    boolean existsByName(String clubName);
    Optional<Club> findByName(String clubName);
}
