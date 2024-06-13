package com.charmroom.charmroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.charmroom.charmroom.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findById(String Id);
}
