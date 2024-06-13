package com.charmroom.charmroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.charmroom.charmroom.entity.User;

public interface UserRepository extends JpaRepository<User, String> {
}
