package com.charmroom.charmroom.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.charmroom.charmroom.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	Optional<User> findByUsername(String username);
	Boolean existsByUsername(String username);
	Optional<User> findByEmail(String email);
	Boolean existsByEmail(String email);
	Boolean existsByNickname(String nickname);
	Page<User> findAll(Pageable pageable);
}
